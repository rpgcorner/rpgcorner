package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.SoldStockAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.SoldStock;
import com.rpgcornerteam.rpgcorner.repository.SoldStockRepository;
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
 * Integration tests for the {@link SoldStockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SoldStockResourceIT {

    private static final Integer DEFAULT_SUPPLIE = 1;
    private static final Integer UPDATED_SUPPLIE = 2;

    private static final Integer DEFAULT_UNIT_PRICE = 1;
    private static final Integer UPDATED_UNIT_PRICE = 2;

    private static final Integer DEFAULT_RETURNED_SUPPLIE = 1;
    private static final Integer UPDATED_RETURNED_SUPPLIE = 2;

    private static final String ENTITY_API_URL = "/api/sold-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SoldStockRepository soldStockRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSoldStockMockMvc;

    private SoldStock soldStock;

    private SoldStock insertedSoldStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SoldStock createEntity() {
        return new SoldStock().supplie(DEFAULT_SUPPLIE).unitPrice(DEFAULT_UNIT_PRICE).returnedSupplie(DEFAULT_RETURNED_SUPPLIE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SoldStock createUpdatedEntity() {
        return new SoldStock().supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE).returnedSupplie(UPDATED_RETURNED_SUPPLIE);
    }

    @BeforeEach
    public void initTest() {
        soldStock = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSoldStock != null) {
            soldStockRepository.delete(insertedSoldStock);
            insertedSoldStock = null;
        }
    }

    @Test
    @Transactional
    void createSoldStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SoldStock
        var returnedSoldStock = om.readValue(
            restSoldStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(soldStock)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SoldStock.class
        );

        // Validate the SoldStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSoldStockUpdatableFieldsEquals(returnedSoldStock, getPersistedSoldStock(returnedSoldStock));

        insertedSoldStock = returnedSoldStock;
    }

    @Test
    @Transactional
    void createSoldStockWithExistingId() throws Exception {
        // Create the SoldStock with an existing ID
        soldStock.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSoldStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(soldStock)))
            .andExpect(status().isBadRequest());

        // Validate the SoldStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSoldStocks() throws Exception {
        // Initialize the database
        insertedSoldStock = soldStockRepository.saveAndFlush(soldStock);

        // Get all the soldStockList
        restSoldStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(soldStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].supplie").value(hasItem(DEFAULT_SUPPLIE)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.[*].returnedSupplie").value(hasItem(DEFAULT_RETURNED_SUPPLIE)));
    }

    @Test
    @Transactional
    void getSoldStock() throws Exception {
        // Initialize the database
        insertedSoldStock = soldStockRepository.saveAndFlush(soldStock);

        // Get the soldStock
        restSoldStockMockMvc
            .perform(get(ENTITY_API_URL_ID, soldStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(soldStock.getId().intValue()))
            .andExpect(jsonPath("$.supplie").value(DEFAULT_SUPPLIE))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE))
            .andExpect(jsonPath("$.returnedSupplie").value(DEFAULT_RETURNED_SUPPLIE));
    }

    @Test
    @Transactional
    void getNonExistingSoldStock() throws Exception {
        // Get the soldStock
        restSoldStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSoldStock() throws Exception {
        // Initialize the database
        insertedSoldStock = soldStockRepository.saveAndFlush(soldStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the soldStock
        SoldStock updatedSoldStock = soldStockRepository.findById(soldStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSoldStock are not directly saved in db
        em.detach(updatedSoldStock);
        updatedSoldStock.supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE).returnedSupplie(UPDATED_RETURNED_SUPPLIE);

        restSoldStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSoldStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSoldStock))
            )
            .andExpect(status().isOk());

        // Validate the SoldStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSoldStockToMatchAllProperties(updatedSoldStock);
    }

    @Test
    @Transactional
    void putNonExistingSoldStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soldStock.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSoldStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, soldStock.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(soldStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the SoldStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSoldStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soldStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoldStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(soldStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the SoldStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSoldStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soldStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoldStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(soldStock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SoldStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSoldStockWithPatch() throws Exception {
        // Initialize the database
        insertedSoldStock = soldStockRepository.saveAndFlush(soldStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the soldStock using partial update
        SoldStock partialUpdatedSoldStock = new SoldStock();
        partialUpdatedSoldStock.setId(soldStock.getId());

        restSoldStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSoldStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSoldStock))
            )
            .andExpect(status().isOk());

        // Validate the SoldStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSoldStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSoldStock, soldStock),
            getPersistedSoldStock(soldStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateSoldStockWithPatch() throws Exception {
        // Initialize the database
        insertedSoldStock = soldStockRepository.saveAndFlush(soldStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the soldStock using partial update
        SoldStock partialUpdatedSoldStock = new SoldStock();
        partialUpdatedSoldStock.setId(soldStock.getId());

        partialUpdatedSoldStock.supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE).returnedSupplie(UPDATED_RETURNED_SUPPLIE);

        restSoldStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSoldStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSoldStock))
            )
            .andExpect(status().isOk());

        // Validate the SoldStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSoldStockUpdatableFieldsEquals(partialUpdatedSoldStock, getPersistedSoldStock(partialUpdatedSoldStock));
    }

    @Test
    @Transactional
    void patchNonExistingSoldStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soldStock.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSoldStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, soldStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(soldStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the SoldStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSoldStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soldStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoldStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(soldStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the SoldStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSoldStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soldStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoldStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(soldStock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SoldStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSoldStock() throws Exception {
        // Initialize the database
        insertedSoldStock = soldStockRepository.saveAndFlush(soldStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the soldStock
        restSoldStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, soldStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return soldStockRepository.count();
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

    protected SoldStock getPersistedSoldStock(SoldStock soldStock) {
        return soldStockRepository.findById(soldStock.getId()).orElseThrow();
    }

    protected void assertPersistedSoldStockToMatchAllProperties(SoldStock expectedSoldStock) {
        assertSoldStockAllPropertiesEquals(expectedSoldStock, getPersistedSoldStock(expectedSoldStock));
    }

    protected void assertPersistedSoldStockToMatchUpdatableProperties(SoldStock expectedSoldStock) {
        assertSoldStockAllUpdatablePropertiesEquals(expectedSoldStock, getPersistedSoldStock(expectedSoldStock));
    }
}
