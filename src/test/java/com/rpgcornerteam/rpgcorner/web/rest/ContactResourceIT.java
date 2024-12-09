package com.rpgcornerteam.rpgcorner.web.rest;

import static com.rpgcornerteam.rpgcorner.domain.ContactAsserts.*;
import static com.rpgcornerteam.rpgcorner.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgcornerteam.rpgcorner.IntegrationTest;
import com.rpgcornerteam.rpgcorner.domain.Contact;
import com.rpgcornerteam.rpgcorner.repository.ContactRepository;
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
 * Integration tests for the {@link ContactResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContactResourceIT {

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TAX_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_TAX_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_FAX = "AAAAAAAAAA";
    private static final String UPDATED_FAX = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/contacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContactMockMvc;

    private Contact contact;

    private Contact insertedContact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contact createEntity() {
        return new Contact()
            .companyName(DEFAULT_COMPANY_NAME)
            .taxNumber(DEFAULT_TAX_NUMBER)
            .contactName(DEFAULT_CONTACT_NAME)
            .address(DEFAULT_ADDRESS)
            .email(DEFAULT_EMAIL)
            .fax(DEFAULT_FAX)
            .mobile(DEFAULT_MOBILE)
            .phone(DEFAULT_PHONE)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contact createUpdatedEntity() {
        return new Contact()
            .companyName(UPDATED_COMPANY_NAME)
            .taxNumber(UPDATED_TAX_NUMBER)
            .contactName(UPDATED_CONTACT_NAME)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .fax(UPDATED_FAX)
            .mobile(UPDATED_MOBILE)
            .phone(UPDATED_PHONE)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        contact = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedContact != null) {
            contactRepository.delete(insertedContact);
            insertedContact = null;
        }
    }

    @Test
    @Transactional
    void createContact() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Contact
        var returnedContact = om.readValue(
            restContactMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contact)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Contact.class
        );

        // Validate the Contact in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertContactUpdatableFieldsEquals(returnedContact, getPersistedContact(returnedContact));

        insertedContact = returnedContact;
    }

    @Test
    @Transactional
    void createContactWithExistingId() throws Exception {
        // Create the Contact with an existing ID
        contact.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contact)))
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllContacts() throws Exception {
        // Initialize the database
        insertedContact = contactRepository.saveAndFlush(contact);

        // Get all the contactList
        restContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contact.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].taxNumber").value(hasItem(DEFAULT_TAX_NUMBER)))
            .andExpect(jsonPath("$.[*].contactName").value(hasItem(DEFAULT_CONTACT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].fax").value(hasItem(DEFAULT_FAX)))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getContact() throws Exception {
        // Initialize the database
        insertedContact = contactRepository.saveAndFlush(contact);

        // Get the contact
        restContactMockMvc
            .perform(get(ENTITY_API_URL_ID, contact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contact.getId().intValue()))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.taxNumber").value(DEFAULT_TAX_NUMBER))
            .andExpect(jsonPath("$.contactName").value(DEFAULT_CONTACT_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.fax").value(DEFAULT_FAX))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingContact() throws Exception {
        // Get the contact
        restContactMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContact() throws Exception {
        // Initialize the database
        insertedContact = contactRepository.saveAndFlush(contact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contact
        Contact updatedContact = contactRepository.findById(contact.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContact are not directly saved in db
        em.detach(updatedContact);
        updatedContact
            .companyName(UPDATED_COMPANY_NAME)
            .taxNumber(UPDATED_TAX_NUMBER)
            .contactName(UPDATED_CONTACT_NAME)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .fax(UPDATED_FAX)
            .mobile(UPDATED_MOBILE)
            .phone(UPDATED_PHONE)
            .note(UPDATED_NOTE);

        restContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedContact.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedContact))
            )
            .andExpect(status().isOk());

        // Validate the Contact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContactToMatchAllProperties(updatedContact);
    }

    @Test
    @Transactional
    void putNonExistingContact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contact.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(put(ENTITY_API_URL_ID, contact.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contact)))
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contact.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contact))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contact.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contact)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContactWithPatch() throws Exception {
        // Initialize the database
        insertedContact = contactRepository.saveAndFlush(contact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contact using partial update
        Contact partialUpdatedContact = new Contact();
        partialUpdatedContact.setId(contact.getId());

        partialUpdatedContact.taxNumber(UPDATED_TAX_NUMBER).email(UPDATED_EMAIL).fax(UPDATED_FAX).mobile(UPDATED_MOBILE);

        restContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContact))
            )
            .andExpect(status().isOk());

        // Validate the Contact in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContactUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedContact, contact), getPersistedContact(contact));
    }

    @Test
    @Transactional
    void fullUpdateContactWithPatch() throws Exception {
        // Initialize the database
        insertedContact = contactRepository.saveAndFlush(contact);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contact using partial update
        Contact partialUpdatedContact = new Contact();
        partialUpdatedContact.setId(contact.getId());

        partialUpdatedContact
            .companyName(UPDATED_COMPANY_NAME)
            .taxNumber(UPDATED_TAX_NUMBER)
            .contactName(UPDATED_CONTACT_NAME)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .fax(UPDATED_FAX)
            .mobile(UPDATED_MOBILE)
            .phone(UPDATED_PHONE)
            .note(UPDATED_NOTE);

        restContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContact))
            )
            .andExpect(status().isOk());

        // Validate the Contact in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContactUpdatableFieldsEquals(partialUpdatedContact, getPersistedContact(partialUpdatedContact));
    }

    @Test
    @Transactional
    void patchNonExistingContact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contact.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contact.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contact))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contact.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contact))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContact() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contact.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contact)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contact in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContact() throws Exception {
        // Initialize the database
        insertedContact = contactRepository.saveAndFlush(contact);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contact
        restContactMockMvc
            .perform(delete(ENTITY_API_URL_ID, contact.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contactRepository.count();
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

    protected Contact getPersistedContact(Contact contact) {
        return contactRepository.findById(contact.getId()).orElseThrow();
    }

    protected void assertPersistedContactToMatchAllProperties(Contact expectedContact) {
        assertContactAllPropertiesEquals(expectedContact, getPersistedContact(expectedContact));
    }

    protected void assertPersistedContactToMatchUpdatableProperties(Contact expectedContact) {
        assertContactAllUpdatablePropertiesEquals(expectedContact, getPersistedContact(expectedContact));
    }
}
