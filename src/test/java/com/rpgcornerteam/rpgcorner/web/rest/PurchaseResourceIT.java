package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.PurchaseAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.Purchase;
import com.rpgcornerteam.rpgcorner.domain.Supplier;
import com.rpgcornerteam.rpgcorner.domain.User;
import com.rpgcornerteam.rpgcorner.repository.PurchaseRepository;
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
 * Integration tests for the {@link PurchaseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseResourceIT {

    private static final LocalDate DEFAULT_PURCHASE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PURCHASE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/purchases";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseMockMvc;

    private Purchase purchase;

    private Purchase insertedPurchase;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Purchase createEntity(EntityManager em) {
        Purchase purchase = new Purchase().purchaseDate(DEFAULT_PURCHASE_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        purchase.setPurchasedByUser(user);
        // Add required entity
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            supplier = SupplierResourceIT.createEntity(em);
            em.persist(supplier);
            em.flush();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        purchase.setPurchasedFromSupplier(supplier);
        return purchase;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Purchase createUpdatedEntity(EntityManager em) {
        Purchase updatedPurchase = new Purchase().purchaseDate(UPDATED_PURCHASE_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedPurchase.setPurchasedByUser(user);
        // Add required entity
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            supplier = SupplierResourceIT.createUpdatedEntity(em);
            em.persist(supplier);
            em.flush();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        updatedPurchase.setPurchasedFromSupplier(supplier);
        return updatedPurchase;
    }

    @BeforeEach
    public void initTest() {
        purchase = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedPurchase != null) {
            purchaseRepository.delete(insertedPurchase);
            insertedPurchase = null;
        }
    }

    @Test
    @Transactional
    void createPurchase() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Purchase
        var returnedPurchase = om.readValue(
            restPurchaseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchase)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Purchase.class
        );

        // Validate the Purchase in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPurchaseUpdatableFieldsEquals(returnedPurchase, getPersistedPurchase(returnedPurchase));

        insertedPurchase = returnedPurchase;
    }

    @Test
    @Transactional
    void createPurchaseWithExistingId() throws Exception {
        // Create the Purchase with an existing ID
        purchase.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchase)))
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPurchases() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchase.getId().intValue())))
            .andExpect(jsonPath("$.[*].purchaseDate").value(hasItem(DEFAULT_PURCHASE_DATE.toString())));
    }

    @Test
    @Transactional
    void getPurchase() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        // Get the purchase
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL_ID, purchase.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchase.getId().intValue()))
            .andExpect(jsonPath("$.purchaseDate").value(DEFAULT_PURCHASE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPurchase() throws Exception {
        // Get the purchase
        restPurchaseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchase() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchase
        Purchase updatedPurchase = purchaseRepository.findById(purchase.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPurchase are not directly saved in db
        em.detach(updatedPurchase);
        updatedPurchase.purchaseDate(UPDATED_PURCHASE_DATE);

        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPurchase.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPurchase))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPurchaseToMatchAllProperties(updatedPurchase);
    }

    @Test
    @Transactional
    void putNonExistingPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchase.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchase))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(purchase))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(purchase)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseWithPatch() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchase using partial update
        Purchase partialUpdatedPurchase = new Purchase();
        partialUpdatedPurchase.setId(purchase.getId());

        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchase.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchase))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPurchase, purchase), getPersistedPurchase(purchase));
    }

    @Test
    @Transactional
    void fullUpdatePurchaseWithPatch() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the purchase using partial update
        Purchase partialUpdatedPurchase = new Purchase();
        partialUpdatedPurchase.setId(purchase.getId());

        partialUpdatedPurchase.purchaseDate(UPDATED_PURCHASE_DATE);

        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchase.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPurchase))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPurchaseUpdatableFieldsEquals(partialUpdatedPurchase, getPersistedPurchase(partialUpdatedPurchase));
    }

    @Test
    @Transactional
    void patchNonExistingPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchase.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchase))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(purchase))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchase() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        purchase.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(purchase)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Purchase in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchase() throws Exception {
        // Initialize the database
        insertedPurchase = purchaseRepository.saveAndFlush(purchase);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the purchase
        restPurchaseMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchase.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return purchaseRepository.count();
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

    protected Purchase getPersistedPurchase(Purchase purchase) {
        return purchaseRepository.findById(purchase.getId()).orElseThrow();
    }

    protected void assertPersistedPurchaseToMatchAllProperties(Purchase expectedPurchase) {
        assertPurchaseAllPropertiesEquals(expectedPurchase, getPersistedPurchase(expectedPurchase));
    }

    protected void assertPersistedPurchaseToMatchUpdatableProperties(Purchase expectedPurchase) {
        assertPurchaseAllUpdatablePropertiesEquals(expectedPurchase, getPersistedPurchase(expectedPurchase));
    }
}
