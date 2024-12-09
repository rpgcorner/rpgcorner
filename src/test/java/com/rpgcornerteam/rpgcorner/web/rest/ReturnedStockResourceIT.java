package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.ReturnedStockAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.ReturnedStock;
import com.rpgcornerteam.rpgcorner.repository.ReturnedStockRepository;
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
 * Integration tests for the {@link ReturnedStockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReturnedStockResourceIT {

    private static final Integer DEFAULT_SUPPLIE = 1;
    private static final Integer UPDATED_SUPPLIE = 2;

    private static final Integer DEFAULT_UNIT_PRICE = 1;
    private static final Integer UPDATED_UNIT_PRICE = 2;

    private static final String ENTITY_API_URL = "/api/returned-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReturnedStockRepository returnedStockRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnedStockMockMvc;

    private ReturnedStock returnedStock;

    private ReturnedStock insertedReturnedStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnedStock createEntity() {
        return new ReturnedStock().supplie(DEFAULT_SUPPLIE).unitPrice(DEFAULT_UNIT_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnedStock createUpdatedEntity() {
        return new ReturnedStock().supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);
    }

    @BeforeEach
    public void initTest() {
        returnedStock = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReturnedStock != null) {
            returnedStockRepository.delete(insertedReturnedStock);
            insertedReturnedStock = null;
        }
    }

    @Test
    @Transactional
    void createReturnedStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReturnedStock
        var returnedReturnedStock = om.readValue(
            restReturnedStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnedStock)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReturnedStock.class
        );

        // Validate the ReturnedStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReturnedStockUpdatableFieldsEquals(returnedReturnedStock, getPersistedReturnedStock(returnedReturnedStock));

        insertedReturnedStock = returnedReturnedStock;
    }

    @Test
    @Transactional
    void createReturnedStockWithExistingId() throws Exception {
        // Create the ReturnedStock with an existing ID
        returnedStock.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnedStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnedStock)))
            .andExpect(status().isBadRequest());

        // Validate the ReturnedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReturnedStocks() throws Exception {
        // Initialize the database
        insertedReturnedStock = returnedStockRepository.saveAndFlush(returnedStock);

        // Get all the returnedStockList
        restReturnedStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnedStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].supplie").value(hasItem(DEFAULT_SUPPLIE)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE)));
    }

    @Test
    @Transactional
    void getReturnedStock() throws Exception {
        // Initialize the database
        insertedReturnedStock = returnedStockRepository.saveAndFlush(returnedStock);

        // Get the returnedStock
        restReturnedStockMockMvc
            .perform(get(ENTITY_API_URL_ID, returnedStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(returnedStock.getId().intValue()))
            .andExpect(jsonPath("$.supplie").value(DEFAULT_SUPPLIE))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE));
    }

    @Test
    @Transactional
    void getNonExistingReturnedStock() throws Exception {
        // Get the returnedStock
        restReturnedStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReturnedStock() throws Exception {
        // Initialize the database
        insertedReturnedStock = returnedStockRepository.saveAndFlush(returnedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnedStock
        ReturnedStock updatedReturnedStock = returnedStockRepository.findById(returnedStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReturnedStock are not directly saved in db
        em.detach(updatedReturnedStock);
        updatedReturnedStock.supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);

        restReturnedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReturnedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedReturnedStock))
            )
            .andExpect(status().isOk());

        // Validate the ReturnedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReturnedStockToMatchAllProperties(updatedReturnedStock);
    }

    @Test
    @Transactional
    void putNonExistingReturnedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedStock.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReturnedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnedStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReturnedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnedStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnedStock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReturnedStockWithPatch() throws Exception {
        // Initialize the database
        insertedReturnedStock = returnedStockRepository.saveAndFlush(returnedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnedStock using partial update
        ReturnedStock partialUpdatedReturnedStock = new ReturnedStock();
        partialUpdatedReturnedStock.setId(returnedStock.getId());

        partialUpdatedReturnedStock.unitPrice(UPDATED_UNIT_PRICE);

        restReturnedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnedStock))
            )
            .andExpect(status().isOk());

        // Validate the ReturnedStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnedStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReturnedStock, returnedStock),
            getPersistedReturnedStock(returnedStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateReturnedStockWithPatch() throws Exception {
        // Initialize the database
        insertedReturnedStock = returnedStockRepository.saveAndFlush(returnedStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnedStock using partial update
        ReturnedStock partialUpdatedReturnedStock = new ReturnedStock();
        partialUpdatedReturnedStock.setId(returnedStock.getId());

        partialUpdatedReturnedStock.supplie(UPDATED_SUPPLIE).unitPrice(UPDATED_UNIT_PRICE);

        restReturnedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnedStock))
            )
            .andExpect(status().isOk());

        // Validate the ReturnedStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnedStockUpdatableFieldsEquals(partialUpdatedReturnedStock, getPersistedReturnedStock(partialUpdatedReturnedStock));
    }

    @Test
    @Transactional
    void patchNonExistingReturnedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedStock.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, returnedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReturnedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnedStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnedStock))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReturnedStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnedStock.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnedStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(returnedStock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnedStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturnedStock() throws Exception {
        // Initialize the database
        insertedReturnedStock = returnedStockRepository.saveAndFlush(returnedStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the returnedStock
        restReturnedStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, returnedStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return returnedStockRepository.count();
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

    protected ReturnedStock getPersistedReturnedStock(ReturnedStock returnedStock) {
        return returnedStockRepository.findById(returnedStock.getId()).orElseThrow();
    }

    protected void assertPersistedReturnedStockToMatchAllProperties(ReturnedStock expectedReturnedStock) {
        assertReturnedStockAllPropertiesEquals(expectedReturnedStock, getPersistedReturnedStock(expectedReturnedStock));
    }

    protected void assertPersistedReturnedStockToMatchUpdatableProperties(ReturnedStock expectedReturnedStock) {
        assertReturnedStockAllUpdatablePropertiesEquals(expectedReturnedStock, getPersistedReturnedStock(expectedReturnedStock));
    }
}
