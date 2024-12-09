package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.InventoryAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.Inventory;
import com.rpgcornerteam.rpgcorner.domain.Ware;
import com.rpgcornerteam.rpgcorner.repository.InventoryRepository;
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
 * Integration tests for the {@link InventoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InventoryResourceIT {

    private static final Integer DEFAULT_SUPPLIE = 1;
    private static final Integer UPDATED_SUPPLIE = 2;

    private static final String ENTITY_API_URL = "/api/inventories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryMockMvc;

    private Inventory inventory;

    private Inventory insertedInventory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventory createEntity(EntityManager em) {
        Inventory inventory = new Inventory().supplie(DEFAULT_SUPPLIE);
        // Add required entity
        Ware ware;
        if (TestUtil.findAll(em, Ware.class).isEmpty()) {
            ware = WareResourceIT.createEntity(em);
            em.persist(ware);
            em.flush();
        } else {
            ware = TestUtil.findAll(em, Ware.class).get(0);
        }
        inventory.setWare(ware);
        return inventory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventory createUpdatedEntity(EntityManager em) {
        Inventory updatedInventory = new Inventory().supplie(UPDATED_SUPPLIE);
        // Add required entity
        Ware ware;
        if (TestUtil.findAll(em, Ware.class).isEmpty()) {
            ware = WareResourceIT.createUpdatedEntity(em);
            em.persist(ware);
            em.flush();
        } else {
            ware = TestUtil.findAll(em, Ware.class).get(0);
        }
        updatedInventory.setWare(ware);
        return updatedInventory;
    }

    @BeforeEach
    public void initTest() {
        inventory = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedInventory != null) {
            inventoryRepository.delete(insertedInventory);
            insertedInventory = null;
        }
    }

    @Test
    @Transactional
    void createInventory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Inventory
        var returnedInventory = om.readValue(
            restInventoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventory)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Inventory.class
        );

        // Validate the Inventory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInventoryUpdatableFieldsEquals(returnedInventory, getPersistedInventory(returnedInventory));

        insertedInventory = returnedInventory;
    }

    @Test
    @Transactional
    void createInventoryWithExistingId() throws Exception {
        // Create the Inventory with an existing ID
        inventory.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventory)))
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInventories() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get all the inventoryList
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].supplie").value(hasItem(DEFAULT_SUPPLIE)));
    }

    @Test
    @Transactional
    void getInventory() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        // Get the inventory
        restInventoryMockMvc
            .perform(get(ENTITY_API_URL_ID, inventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventory.getId().intValue()))
            .andExpect(jsonPath("$.supplie").value(DEFAULT_SUPPLIE));
    }

    @Test
    @Transactional
    void getNonExistingInventory() throws Exception {
        // Get the inventory
        restInventoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventory() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInventory are not directly saved in db
        em.detach(updatedInventory);
        updatedInventory.supplie(UPDATED_SUPPLIE);

        restInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInventory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInventory))
            )
            .andExpect(status().isOk());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryToMatchAllProperties(updatedInventory);
    }

    @Test
    @Transactional
    void putNonExistingInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventory.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventory))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventory))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryWithPatch() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory using partial update
        Inventory partialUpdatedInventory = new Inventory();
        partialUpdatedInventory.setId(inventory.getId());

        partialUpdatedInventory.supplie(UPDATED_SUPPLIE);

        restInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventory))
            )
            .andExpect(status().isOk());

        // Validate the Inventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventory, inventory),
            getPersistedInventory(inventory)
        );
    }

    @Test
    @Transactional
    void fullUpdateInventoryWithPatch() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventory using partial update
        Inventory partialUpdatedInventory = new Inventory();
        partialUpdatedInventory.setId(inventory.getId());

        partialUpdatedInventory.supplie(UPDATED_SUPPLIE);

        restInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventory))
            )
            .andExpect(status().isOk());

        // Validate the Inventory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryUpdatableFieldsEquals(partialUpdatedInventory, getPersistedInventory(partialUpdatedInventory));
    }

    @Test
    @Transactional
    void patchNonExistingInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventory))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventory))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inventory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inventory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventory() throws Exception {
        // Initialize the database
        insertedInventory = inventoryRepository.saveAndFlush(inventory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventory
        restInventoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inventoryRepository.count();
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

    protected Inventory getPersistedInventory(Inventory inventory) {
        return inventoryRepository.findById(inventory.getId()).orElseThrow();
    }

    protected void assertPersistedInventoryToMatchAllProperties(Inventory expectedInventory) {
        assertInventoryAllPropertiesEquals(expectedInventory, getPersistedInventory(expectedInventory));
    }

    protected void assertPersistedInventoryToMatchUpdatableProperties(Inventory expectedInventory) {
        assertInventoryAllUpdatablePropertiesEquals(expectedInventory, getPersistedInventory(expectedInventory));
    }
}
