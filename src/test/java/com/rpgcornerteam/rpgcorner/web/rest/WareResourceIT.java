package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.WareAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.Category;
import com.rpgcornerteam.rpgcorner.domain.Ware;
import com.rpgcornerteam.rpgcorner.repository.WareRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WareResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WareResourceIT {

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PRODUCT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/wares";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WareRepository wareRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWareMockMvc;

    private Ware ware;

    private Ware insertedWare;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ware createEntity(EntityManager em) {
        Ware ware = new Ware()
            .active(DEFAULT_ACTIVE)
            .created(DEFAULT_CREATED)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .productCode(DEFAULT_PRODUCT_CODE);
        // Add required entity
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createEntity();
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        ware.setMainCategory(category);
        return ware;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ware createUpdatedEntity(EntityManager em) {
        Ware updatedWare = new Ware()
            .active(UPDATED_ACTIVE)
            .created(UPDATED_CREATED)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .productCode(UPDATED_PRODUCT_CODE);
        // Add required entity
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createUpdatedEntity();
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        updatedWare.setMainCategory(category);
        return updatedWare;
    }

    @BeforeEach
    public void initTest() {
        ware = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedWare != null) {
            wareRepository.delete(insertedWare);
            insertedWare = null;
        }
    }

    @Test
    @Transactional
    void createWare() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Ware
        var returnedWare = om.readValue(
            restWareMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ware)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Ware.class
        );

        // Validate the Ware in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertWareUpdatableFieldsEquals(returnedWare, getPersistedWare(returnedWare));

        insertedWare = returnedWare;
    }

    @Test
    @Transactional
    void createWareWithExistingId() throws Exception {
        // Create the Ware with an existing ID
        ware.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWareMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ware)))
            .andExpect(status().isBadRequest());

        // Validate the Ware in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWares() throws Exception {
        // Initialize the database
        insertedWare = wareRepository.saveAndFlush(ware);

        // Get all the wareList
        restWareMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ware.getId().intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].productCode").value(hasItem(DEFAULT_PRODUCT_CODE)));
    }

    @Test
    @Transactional
    void getWare() throws Exception {
        // Initialize the database
        insertedWare = wareRepository.saveAndFlush(ware);

        // Get the ware
        restWareMockMvc
            .perform(get(ENTITY_API_URL_ID, ware.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ware.getId().intValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.productCode").value(DEFAULT_PRODUCT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingWare() throws Exception {
        // Get the ware
        restWareMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWare() throws Exception {
        // Initialize the database
        insertedWare = wareRepository.saveAndFlush(ware);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ware
        Ware updatedWare = wareRepository.findById(ware.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWare are not directly saved in db
        em.detach(updatedWare);
        updatedWare
            .active(UPDATED_ACTIVE)
            .created(UPDATED_CREATED)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .productCode(UPDATED_PRODUCT_CODE);

        restWareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWare.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedWare))
            )
            .andExpect(status().isOk());

        // Validate the Ware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWareToMatchAllProperties(updatedWare);
    }

    @Test
    @Transactional
    void putNonExistingWare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ware.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWareMockMvc
            .perform(put(ENTITY_API_URL_ID, ware.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ware)))
            .andExpect(status().isBadRequest());

        // Validate the Ware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ware.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ware))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ware.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWareMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ware)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWareWithPatch() throws Exception {
        // Initialize the database
        insertedWare = wareRepository.saveAndFlush(ware);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ware using partial update
        Ware partialUpdatedWare = new Ware();
        partialUpdatedWare.setId(ware.getId());

        partialUpdatedWare.created(UPDATED_CREATED).name(UPDATED_NAME).productCode(UPDATED_PRODUCT_CODE);

        restWareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWare.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWare))
            )
            .andExpect(status().isOk());

        // Validate the Ware in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWareUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedWare, ware), getPersistedWare(ware));
    }

    @Test
    @Transactional
    void fullUpdateWareWithPatch() throws Exception {
        // Initialize the database
        insertedWare = wareRepository.saveAndFlush(ware);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ware using partial update
        Ware partialUpdatedWare = new Ware();
        partialUpdatedWare.setId(ware.getId());

        partialUpdatedWare
            .active(UPDATED_ACTIVE)
            .created(UPDATED_CREATED)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .productCode(UPDATED_PRODUCT_CODE);

        restWareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWare.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWare))
            )
            .andExpect(status().isOk());

        // Validate the Ware in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWareUpdatableFieldsEquals(partialUpdatedWare, getPersistedWare(partialUpdatedWare));
    }

    @Test
    @Transactional
    void patchNonExistingWare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ware.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWareMockMvc
            .perform(patch(ENTITY_API_URL_ID, ware.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ware)))
            .andExpect(status().isBadRequest());

        // Validate the Ware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ware.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ware))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ware.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWareMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ware)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ware in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWare() throws Exception {
        // Initialize the database
        insertedWare = wareRepository.saveAndFlush(ware);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ware
        restWareMockMvc
            .perform(delete(ENTITY_API_URL_ID, ware.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return wareRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Ware getPersistedWare(Ware ware) {
        return wareRepository.findById(ware.getId()).orElseThrow();
    }

    protected void assertPersistedWareToMatchAllProperties(Ware expectedWare) {
        assertWareAllPropertiesEquals(expectedWare, getPersistedWare(expectedWare));
    }

    protected void assertPersistedWareToMatchUpdatableProperties(Ware expectedWare) {
        assertWareAllUpdatablePropertiesEquals(expectedWare, getPersistedWare(expectedWare));
    }
}
