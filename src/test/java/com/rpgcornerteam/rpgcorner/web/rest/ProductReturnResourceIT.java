package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.ProductReturnAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.ProductReturn;
import com.rpgcornerteam.rpgcorner.repository.ProductReturnRepository;
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
 * Integration tests for the {@link ProductReturnResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductReturnResourceIT {

    private static final LocalDate DEFAULT_RETURN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RETURN_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-returns";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductReturnRepository productReturnRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductReturnMockMvc;

    private ProductReturn productReturn;

    private ProductReturn insertedProductReturn;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductReturn createEntity(EntityManager em) {
        ProductReturn productReturn = new ProductReturn().returnDate(DEFAULT_RETURN_DATE).note(DEFAULT_NOTE);
        return productReturn;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductReturn createUpdatedEntity(EntityManager em) {
        ProductReturn updatedProductReturn = new ProductReturn().returnDate(UPDATED_RETURN_DATE).note(UPDATED_NOTE);
        return updatedProductReturn;
    }

    @BeforeEach
    public void initTest() {
        productReturn = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductReturn != null) {
            productReturnRepository.delete(insertedProductReturn);
            insertedProductReturn = null;
        }
    }

    @Test
    @Transactional
    void createProductReturn() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductReturn
        var returnedProductReturn = om.readValue(
            restProductReturnMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productReturn)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductReturn.class
        );

        // Validate the ProductReturn in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProductReturnUpdatableFieldsEquals(returnedProductReturn, getPersistedProductReturn(returnedProductReturn));

        insertedProductReturn = returnedProductReturn;
    }

    @Test
    @Transactional
    void createProductReturnWithExistingId() throws Exception {
        // Create the ProductReturn with an existing ID
        productReturn.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductReturnMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productReturn)))
            .andExpect(status().isBadRequest());

        // Validate the ProductReturn in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProductReturns() throws Exception {
        // Initialize the database
        insertedProductReturn = productReturnRepository.saveAndFlush(productReturn);

        // Get all the productReturnList
        restProductReturnMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productReturn.getId().intValue())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getProductReturn() throws Exception {
        // Initialize the database
        insertedProductReturn = productReturnRepository.saveAndFlush(productReturn);

        // Get the productReturn
        restProductReturnMockMvc
            .perform(get(ENTITY_API_URL_ID, productReturn.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productReturn.getId().intValue()))
            .andExpect(jsonPath("$.returnDate").value(DEFAULT_RETURN_DATE.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingProductReturn() throws Exception {
        // Get the productReturn
        restProductReturnMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductReturn() throws Exception {
        // Initialize the database
        insertedProductReturn = productReturnRepository.saveAndFlush(productReturn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productReturn
        ProductReturn updatedProductReturn = productReturnRepository.findById(productReturn.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductReturn are not directly saved in db
        em.detach(updatedProductReturn);
        updatedProductReturn.returnDate(UPDATED_RETURN_DATE).note(UPDATED_NOTE);

        restProductReturnMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProductReturn.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProductReturn))
            )
            .andExpect(status().isOk());

        // Validate the ProductReturn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductReturnToMatchAllProperties(updatedProductReturn);
    }

    @Test
    @Transactional
    void putNonExistingProductReturn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productReturn.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductReturnMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productReturn.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productReturn))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductReturn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductReturn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productReturn.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductReturnMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productReturn))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductReturn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductReturn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productReturn.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductReturnMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productReturn)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductReturn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductReturnWithPatch() throws Exception {
        // Initialize the database
        insertedProductReturn = productReturnRepository.saveAndFlush(productReturn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productReturn using partial update
        ProductReturn partialUpdatedProductReturn = new ProductReturn();
        partialUpdatedProductReturn.setId(productReturn.getId());

        partialUpdatedProductReturn.returnDate(UPDATED_RETURN_DATE).note(UPDATED_NOTE);

        restProductReturnMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductReturn.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductReturn))
            )
            .andExpect(status().isOk());

        // Validate the ProductReturn in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductReturnUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductReturn, productReturn),
            getPersistedProductReturn(productReturn)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductReturnWithPatch() throws Exception {
        // Initialize the database
        insertedProductReturn = productReturnRepository.saveAndFlush(productReturn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productReturn using partial update
        ProductReturn partialUpdatedProductReturn = new ProductReturn();
        partialUpdatedProductReturn.setId(productReturn.getId());

        partialUpdatedProductReturn.returnDate(UPDATED_RETURN_DATE).note(UPDATED_NOTE);

        restProductReturnMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductReturn.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductReturn))
            )
            .andExpect(status().isOk());

        // Validate the ProductReturn in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductReturnUpdatableFieldsEquals(partialUpdatedProductReturn, getPersistedProductReturn(partialUpdatedProductReturn));
    }

    @Test
    @Transactional
    void patchNonExistingProductReturn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productReturn.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductReturnMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productReturn.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productReturn))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductReturn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductReturn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productReturn.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductReturnMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productReturn))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductReturn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductReturn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productReturn.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductReturnMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productReturn)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductReturn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductReturn() throws Exception {
        // Initialize the database
        insertedProductReturn = productReturnRepository.saveAndFlush(productReturn);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productReturn
        restProductReturnMockMvc
            .perform(delete(ENTITY_API_URL_ID, productReturn.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productReturnRepository.count();
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

    protected ProductReturn getPersistedProductReturn(ProductReturn productReturn) {
        return productReturnRepository.findById(productReturn.getId()).orElseThrow();
    }

    protected void assertPersistedProductReturnToMatchAllProperties(ProductReturn expectedProductReturn) {
        assertProductReturnAllPropertiesEquals(expectedProductReturn, getPersistedProductReturn(expectedProductReturn));
    }

    protected void assertPersistedProductReturnToMatchUpdatableProperties(ProductReturn expectedProductReturn) {
        assertProductReturnAllUpdatablePropertiesEquals(expectedProductReturn, getPersistedProductReturn(expectedProductReturn));
    }
}
