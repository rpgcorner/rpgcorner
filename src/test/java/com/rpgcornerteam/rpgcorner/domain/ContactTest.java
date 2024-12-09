package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.ContactTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.CustomerTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Contact.class);
        Contact contact1 = getContactSample1();
        Contact contact2 = new Contact();
        assertThat(contact1).isNotEqualTo(contact2);

        contact2.setId(contact1.getId());
        assertThat(contact1).isEqualTo(contact2);

        contact2 = getContactSample2();
        assertThat(contact1).isNotEqualTo(contact2);
    }

    @Test
    void supplierTest() {
        Contact contact = getContactRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        contact.setSupplier(supplierBack);
        assertThat(contact.getSupplier()).isEqualTo(supplierBack);

        contact.supplier(null);
        assertThat(contact.getSupplier()).isNull();
    }

    @Test
    void customerTest() {
        Contact contact = getContactRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        contact.setCustomer(customerBack);
        assertThat(contact.getCustomer()).isEqualTo(customerBack);
        assertThat(customerBack.getContact()).isEqualTo(contact);

        contact.customer(null);
        assertThat(contact.getCustomer()).isNull();
        assertThat(customerBack.getContact()).isNull();
    }
}
