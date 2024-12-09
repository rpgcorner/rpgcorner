package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.ContactTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.PurchaseTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SupplierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Supplier.class);
        Supplier supplier1 = getSupplierSample1();
        Supplier supplier2 = new Supplier();
        assertThat(supplier1).isNotEqualTo(supplier2);

        supplier2.setId(supplier1.getId());
        assertThat(supplier1).isEqualTo(supplier2);

        supplier2 = getSupplierSample2();
        assertThat(supplier1).isNotEqualTo(supplier2);
    }

    @Test
    void hashCodeVerifier() {
        Supplier supplier = new Supplier();
        assertThat(supplier.hashCode()).isZero();

        Supplier supplier1 = getSupplierSample1();
        supplier.setId(supplier1.getId());
        assertThat(supplier).hasSameHashCodeAs(supplier1);
    }

    @Test
    void contactTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        Contact contactBack = getContactRandomSampleGenerator();

        supplier.addContact(contactBack);
        assertThat(supplier.getContacts()).containsOnly(contactBack);
        assertThat(contactBack.getSupplier()).isEqualTo(supplier);

        supplier.removeContact(contactBack);
        assertThat(supplier.getContacts()).doesNotContain(contactBack);
        assertThat(contactBack.getSupplier()).isNull();

        supplier.contacts(new HashSet<>(Set.of(contactBack)));
        assertThat(supplier.getContacts()).containsOnly(contactBack);
        assertThat(contactBack.getSupplier()).isEqualTo(supplier);

        supplier.setContacts(new HashSet<>());
        assertThat(supplier.getContacts()).doesNotContain(contactBack);
        assertThat(contactBack.getSupplier()).isNull();
    }

    @Test
    void purchaseTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        Purchase purchaseBack = getPurchaseRandomSampleGenerator();

        supplier.addPurchase(purchaseBack);
        assertThat(supplier.getPurchases()).containsOnly(purchaseBack);
        assertThat(purchaseBack.getPurchasedFromSupplier()).isEqualTo(supplier);

        supplier.removePurchase(purchaseBack);
        assertThat(supplier.getPurchases()).doesNotContain(purchaseBack);
        assertThat(purchaseBack.getPurchasedFromSupplier()).isNull();

        supplier.purchases(new HashSet<>(Set.of(purchaseBack)));
        assertThat(supplier.getPurchases()).containsOnly(purchaseBack);
        assertThat(purchaseBack.getPurchasedFromSupplier()).isEqualTo(supplier);

        supplier.setPurchases(new HashSet<>());
        assertThat(supplier.getPurchases()).doesNotContain(purchaseBack);
        assertThat(purchaseBack.getPurchasedFromSupplier()).isNull();
    }
}
