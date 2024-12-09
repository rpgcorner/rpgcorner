package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.DisposeAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.Dispose;
import com.rpgcornerteam.rpgcorner.domain.User;
import com.rpgcornerteam.rpgcorner.repository.DisposeRepository;
import com.rpgcornerteam.rpgcorner.repository.UserRepository;
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
 * Integration tests for the {@link DisposeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DisposeResourceIT {

    private static final LocalDate DEFAULT_DISPOSE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DISPOSE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/disposes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DisposeRepository disposeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDisposeMockMvc;

    private Dispose dispose;

    private Dispose insertedDispose;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dispose createEntity(EntityManager em) {
        Dispose dispose = new Dispose().disposeDate(DEFAULT_DISPOSE_DATE).note(DEFAULT_NOTE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        dispose.setDisposedByUser(user);
        return dispose;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dispose createUpdatedEntity(EntityManager em) {
        Dispose updatedDispose = new Dispose().disposeDate(UPDATED_DISPOSE_DATE).note(UPDATED_NOTE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedDispose.setDisposedByUser(user);
        return updatedDispose;
    }

    @BeforeEach
    public void initTest() {
        dispose = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedDispose != null) {
            disposeRepository.delete(insertedDispose);
            insertedDispose = null;
        }
    }

    @Test
    @Transactional
    void createDispose() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Dispose
        var returnedDispose = om.readValue(
            restDisposeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dispose)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Dispose.class
        );

        // Validate the Dispose in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDisposeUpdatableFieldsEquals(returnedDispose, getPersistedDispose(returnedDispose));

        insertedDispose = returnedDispose;
    }

    @Test
    @Transactional
    void createDisposeWithExistingId() throws Exception {
        // Create the Dispose with an existing ID
        dispose.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDisposeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dispose)))
            .andExpect(status().isBadRequest());

        // Validate the Dispose in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDisposes() throws Exception {
        // Initialize the database
        insertedDispose = disposeRepository.saveAndFlush(dispose);

        // Get all the disposeList
        restDisposeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dispose.getId().intValue())))
            .andExpect(jsonPath("$.[*].disposeDate").value(hasItem(DEFAULT_DISPOSE_DATE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getDispose() throws Exception {
        // Initialize the database
        insertedDispose = disposeRepository.saveAndFlush(dispose);

        // Get the dispose
        restDisposeMockMvc
            .perform(get(ENTITY_API_URL_ID, dispose.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dispose.getId().intValue()))
            .andExpect(jsonPath("$.disposeDate").value(DEFAULT_DISPOSE_DATE.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingDispose() throws Exception {
        // Get the dispose
        restDisposeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDispose() throws Exception {
        // Initialize the database
        insertedDispose = disposeRepository.saveAndFlush(dispose);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispose
        Dispose updatedDispose = disposeRepository.findById(dispose.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDispose are not directly saved in db
        em.detach(updatedDispose);
        updatedDispose.disposeDate(UPDATED_DISPOSE_DATE).note(UPDATED_NOTE);

        restDisposeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDispose.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDispose))
            )
            .andExpect(status().isOk());

        // Validate the Dispose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDisposeToMatchAllProperties(updatedDispose);
    }

    @Test
    @Transactional
    void putNonExistingDispose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispose.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisposeMockMvc
            .perform(put(ENTITY_API_URL_ID, dispose.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dispose)))
            .andExpect(status().isBadRequest());

        // Validate the Dispose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDispose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispose.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisposeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dispose))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dispose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDispose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispose.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisposeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dispose)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dispose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDisposeWithPatch() throws Exception {
        // Initialize the database
        insertedDispose = disposeRepository.saveAndFlush(dispose);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispose using partial update
        Dispose partialUpdatedDispose = new Dispose();
        partialUpdatedDispose.setId(dispose.getId());

        partialUpdatedDispose.disposeDate(UPDATED_DISPOSE_DATE);

        restDisposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDispose.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDispose))
            )
            .andExpect(status().isOk());

        // Validate the Dispose in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisposeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDispose, dispose), getPersistedDispose(dispose));
    }

    @Test
    @Transactional
    void fullUpdateDisposeWithPatch() throws Exception {
        // Initialize the database
        insertedDispose = disposeRepository.saveAndFlush(dispose);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispose using partial update
        Dispose partialUpdatedDispose = new Dispose();
        partialUpdatedDispose.setId(dispose.getId());

        partialUpdatedDispose.disposeDate(UPDATED_DISPOSE_DATE).note(UPDATED_NOTE);

        restDisposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDispose.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDispose))
            )
            .andExpect(status().isOk());

        // Validate the Dispose in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisposeUpdatableFieldsEquals(partialUpdatedDispose, getPersistedDispose(partialUpdatedDispose));
    }

    @Test
    @Transactional
    void patchNonExistingDispose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispose.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dispose.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dispose))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dispose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDispose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispose.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisposeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dispose))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dispose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDispose() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispose.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisposeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dispose)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dispose in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDispose() throws Exception {
        // Initialize the database
        insertedDispose = disposeRepository.saveAndFlush(dispose);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dispose
        restDisposeMockMvc
            .perform(delete(ENTITY_API_URL_ID, dispose.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return disposeRepository.count();
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

    protected Dispose getPersistedDispose(Dispose dispose) {
        return disposeRepository.findById(dispose.getId()).orElseThrow();
    }

    protected void assertPersistedDisposeToMatchAllProperties(Dispose expectedDispose) {
        assertDisposeAllPropertiesEquals(expectedDispose, getPersistedDispose(expectedDispose));
    }

    protected void assertPersistedDisposeToMatchUpdatableProperties(Dispose expectedDispose) {
        assertDisposeAllUpdatablePropertiesEquals(expectedDispose, getPersistedDispose(expectedDispose));
    }
}
