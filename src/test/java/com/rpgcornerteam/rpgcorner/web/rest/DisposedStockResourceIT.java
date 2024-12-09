package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.DisposedStockAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.DisposedStock;
import com.rpgcornerteam.rpgcorner.repository.DisposedStockRepository;
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
 * Integration tests for the {@link DisposedStockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DisposedStockResourceIT {

    private static final Integer DEFAULT_SUPPLIE = 1;
    private static final Integer UPDATED_SUPPLIE = 2;

    private static final Integer DEFAULT_UNIT_PRICE = 1;
    private static final Integer UPDATED_UNIT_PRICE = 2;

    private static final String ENTITY_API_URL = "/api/disposed-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DisposedStockRepository disposedStockRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDisposedStockMockMvc;

    private DisposedStock disposedStock;

    private DisposedStock insertedDisposedStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DisposedStock createEntity() {
        return new DisposedStock().supplie(DEFAULT_SUPPLIE).unitPrice(DEFAULT_UNIT_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DisposedStock createUpdatedEntity() {
        return new DisposedStock().supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);
    }

    @BeforeEach
    public void initTest() {
        disposedStock = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDisposedStock != null) {
            disposedStockRepository.delete(insertedDisposedStock);
            insertedDisposedStock = null;
        }
    }

    @Test
    @Transactional
    void createDisposedStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DisposedStock
        var returnedDisposedStock = om.readValue(
            restDisposedStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disposedStock)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DisposedStock.class
        );

        // Validate the DisposedStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDisposedStockUpdatableFieldsEquals(returnedDisposedStock, getPersistedDisposedStock(returnedDisposedStock));

        insertedDisposedStock = returnedDisposedStock;
    }

    @Test
    @Transactional
    void createDisposedStockWithExistingId() throws Exception {
        // Create the DisposedStock with an existing ID
        disposedStock.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDisposedStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disposedStock)))
            .andExpect(status().isBadRequest());

        // Validate the DisposedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDisposedStocks() throws Exception {
        // Initialize the database
        insertedDisposedStock = disposedStockRepository.saveAndFlush(disposedStock);

        // Get all the disposedStockList
        restDisposedStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(disposedStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].supplie").value(hasItem(DEFAULT_SUPPLIE)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE)));
    }

    @Test
    @Transactional
    void getDisposedStock() throws Exception {
        // Initialize the database
        insertedDisposedStock = disposedStockRepository.saveAndFlush(disposedStock);

        // Get the disposedStock
        restDisposedStockMockMvc
            .perform(get(ENTITY_API_URL_ID, disposedStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(disposedStock.getId().intValue()))
            .andExpect(jsonPath("$.supplie").value(DEFAULT_SUPPLIE))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE));
    }

    @Test
    @Transactional
    void getNonExistingDisposedStock() throws Exception {
        // Get the disposedStock
        restDisposedStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDisposedStock() throws Exception {
        // Initialize the database
        insertedDisposedStock = disposedStockRepository.saveAndFlush(disposedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disposedStock
        DisposedStock updatedDisposedStock = disposedStockRepository.findById(disposedStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDisposedStock are not directly saved in db
        em.detach(updatedDisposedStock);
        updatedDisposedStock.supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);

        restDisposedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDisposedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDisposedStock))
            )
            .andExpect(status().isOk());

        // Validate the DisposedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDisposedStockToMatchAllProperties(updatedDisposedStock);
    }

    @Test
    @Transactional
    void putNonExistingDisposedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disposedStock.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisposedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, disposedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disposedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the DisposedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDisposedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disposedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisposedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(disposedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the DisposedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDisposedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disposedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisposedStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(disposedStock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DisposedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDisposedStockWithPatch() throws Exception {
        // Initialize the database
        insertedDisposedStock = disposedStockRepository.saveAndFlush(disposedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disposedStock using partial update
        DisposedStock partialUpdatedDisposedStock = new DisposedStock();
        partialUpdatedDisposedStock.setId(disposedStock.getId());

        partialUpdatedDisposedStock.unitPrice(UPDATED_UNIT_PRICE);

        restDisposedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDisposedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDisposedStock))
            )
            .andExpect(status().isOk());

        // Validate the DisposedStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisposedStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDisposedStock, disposedStock),
            getPersistedDisposedStock(disposedStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateDisposedStockWithPatch() throws Exception {
        // Initialize the database
        insertedDisposedStock = disposedStockRepository.saveAndFlush(disposedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the disposedStock using partial update
        DisposedStock partialUpdatedDisposedStock = new DisposedStock();
        partialUpdatedDisposedStock.setId(disposedStock.getId());

        partialUpdatedDisposedStock.supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);

        restDisposedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDisposedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDisposedStock))
            )
            .andExpect(status().isOk());

        // Validate the DisposedStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisposedStockUpdatableFieldsEquals(partialUpdatedDisposedStock, getPersistedDisposedStock(partialUpdatedDisposedStock));
    }

    @Test
    @Transactional
    void patchNonExistingDisposedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disposedStock.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDisposedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, disposedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(disposedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the DisposedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDisposedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disposedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisposedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(disposedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the DisposedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDisposedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        disposedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDisposedStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(disposedStock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DisposedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDisposedStock() throws Exception {
        // Initialize the database
        insertedDisposedStock = disposedStockRepository.saveAndFlush(disposedStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the disposedStock
        restDisposedStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, disposedStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return disposedStockRepository.count();
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

    protected DisposedStock getPersistedDisposedStock(DisposedStock disposedStock) {
        return disposedStockRepository.findById(disposedStock.getId()).orElseThrow();
    }

    protected void assertPersistedDisposedStockToMatchAllProperties(DisposedStock expectedDisposedStock) {
        assertDisposedStockAllPropertiesEquals(expectedDisposedStock, getPersistedDisposedStock(expectedDisposedStock));
    }

    protected void assertPersistedDisposedStockToMatchUpdatableProperties(DisposedStock expectedDisposedStock) {
        assertDisposedStockAllUpdatablePropertiesEquals(expectedDisposedStock, getPersistedDisposedStock(expectedDisposedStock));
    }
}
