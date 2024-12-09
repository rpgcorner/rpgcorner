package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.PurchasedStockAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.PurchasedStock;
import com.rpgcornerteam.rpgcorner.repository.PurchasedStockRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PurchasedStockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchasedStockResourceIT {

    private static final Integer DEFAULT_SUPPLIE = 1;
    private static final Integer UPDATED_SUPPLIE = 2;

    private static final Integer DEFAULT_UNIT_PRICE = 1;
    private static final Integer UPDATED_UNIT_PRICE = 2;

    private static final String ENTITY_API_URL = "/api/purchased-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PurchasedStockRepository purchasedStockRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchasedStockMockMvc;

    private PurchasedStock purchasedStock;

    private PurchasedStock insertedPurchasedStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchasedStock createEntity() {
        return new PurchasedStock().supplie(DEFAULT_SUPPLIE).unitPrice(DEFAULT_UNIT_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchasedStock createUpdatedEntity() {
        return new PurchasedStock().supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);
    }

    @BeforeEach
    public void initTest() {
        purchasedStock = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPurchasedStock != null) {
            purchasedStockRepository.delete(insertedPurchasedStock);
            insertedPurchasedStock = null;
        }
    }

    @Test
    @Transactional
    void createPurchasedStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PurchasedStock
        var returnedPurchasedStock = om.readValue(
            restPurchasedStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchasedStock)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PurchasedStock.class
        );

        // Validate the PurchasedStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPurchasedStockUpdatableFieldsEquals(returnedPurchasedStock, getPersistedPurchasedStock(returnedPurchasedStock));

        insertedPurchasedStock = returnedPurchasedStock;
    }

    @Test
    @Transactional
    void createPurchasedStockWithExistingId() throws Exception {
        // Create the PurchasedStock with an existing ID
        purchasedStock.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchasedStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchasedStock)))
            .andExpect(status().isBadRequest());

        // Validate the PurchasedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPurchasedStocks() throws Exception {
        // Initialize the database
        insertedPurchasedStock = purchasedStockRepository.saveAndFlush(purchasedStock);

        // Get all the purchasedStockList
        restPurchasedStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchasedStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].supplie").value(hasItem(DEFAULT_SUPPLIE)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE)));
    }

    @Test
    @Transactional
    void getPurchasedStock() throws Exception {
        // Initialize the database
        insertedPurchasedStock = purchasedStockRepository.saveAndFlush(purchasedStock);

        // Get the purchasedStock
        restPurchasedStockMockMvc
            .perform(get(ENTITY_API_URL_ID, purchasedStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchasedStock.getId().intValue()))
            .andExpect(jsonPath("$.supplie").value(DEFAULT_SUPPLIE))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE));
    }

    @Test
    @Transactional
    void getNonExistingPurchasedStock() throws Exception {
        // Get the purchasedStock
        restPurchasedStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchasedStock() throws Exception {
        // Initialize the database
        insertedPurchasedStock = purchasedStockRepository.saveAndFlush(purchasedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchasedStock
        PurchasedStock updatedPurchasedStock = purchasedStockRepository.findById(purchasedStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPurchasedStock are not directly saved in db
        em.detach(updatedPurchasedStock);
        updatedPurchasedStock.supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);

        restPurchasedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPurchasedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPurchasedStock))
            )
            .andExpect(status().isOk());

        // Validate the PurchasedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPurchasedStockToMatchAllProperties(updatedPurchasedStock);
    }

    @Test
    @Transactional
    void putNonExistingPurchasedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasedStock.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchasedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchasedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchasedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchasedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchasedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchasedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchasedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchasedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchasedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchasedStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchasedStock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchasedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchasedStockWithPatch() throws Exception {
        // Initialize the database
        insertedPurchasedStock = purchasedStockRepository.saveAndFlush(purchasedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchasedStock using partial update
        PurchasedStock partialUpdatedPurchasedStock = new PurchasedStock();
        partialUpdatedPurchasedStock.setId(purchasedStock.getId());

        restPurchasedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchasedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchasedStock))
            )
            .andExpect(status().isOk());

        // Validate the PurchasedStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchasedStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPurchasedStock, purchasedStock),
            getPersistedPurchasedStock(purchasedStock)
        );
    }

    @Test
    @Transactional
    void fullUpdatePurchasedStockWithPatch() throws Exception {
        // Initialize the database
        insertedPurchasedStock = purchasedStockRepository.saveAndFlush(purchasedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchasedStock using partial update
        PurchasedStock partialUpdatedPurchasedStock = new PurchasedStock();
        partialUpdatedPurchasedStock.setId(purchasedStock.getId());

        partialUpdatedPurchasedStock.supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);

        restPurchasedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchasedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchasedStock))
            )
            .andExpect(status().isOk());

        // Validate the PurchasedStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchasedStockUpdatableFieldsEquals(partialUpdatedPurchasedStock, getPersistedPurchasedStock(partialUpdatedPurchasedStock));
    }

    @Test
    @Transactional
    void patchNonExistingPurchasedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasedStock.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchasedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchasedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchasedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchasedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchasedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchasedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchasedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchasedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchasedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchasedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchasedStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(purchasedStock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchasedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchasedStock() throws Exception {
        // Initialize the database
        insertedPurchasedStock = purchasedStockRepository.saveAndFlush(purchasedStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the purchasedStock
        restPurchasedStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchasedStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return purchasedStockRepository.count();
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

    protected PurchasedStock getPersistedPurchasedStock(PurchasedStock purchasedStock) {
        return purchasedStockRepository.findById(purchasedStock.getId()).orElseThrow();
    }

    protected void assertPersistedPurchasedStockToMatchAllProperties(PurchasedStock expectedPurchasedStock) {
        assertPurchasedStockAllPropertiesEquals(expectedPurchasedStock, getPersistedPurchasedStock(expectedPurchasedStock));
    }

    protected void assertPersistedPurchasedStockToMatchUpdatableProperties(PurchasedStock expectedPurchasedStock) {
        assertPurchasedStockAllUpdatablePropertiesEquals(expectedPurchasedStock, getPersistedPurchasedStock(expectedPurchasedStock));
    }
}
