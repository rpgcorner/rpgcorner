package com.rpgcornerteam.rpgcorner.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class PurchasedStockAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPurchasedStockAllPropertiesEquals(PurchasedStock expected, PurchasedStock actual) {
        assertPurchasedStockAutoGeneratedPropertiesEquals(expected, actual);
        assertPurchasedStockAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPurchasedStockAllUpdatablePropertiesEquals(PurchasedStock expected, PurchasedStock actual) {
        assertPurchasedStockUpdatableFieldsEquals(expected, actual);
        assertPurchasedStockUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPurchasedStockAutoGeneratedPropertiesEquals(PurchasedStock expected, PurchasedStock actual) {
        assertThat(expected)
            .as("Verify PurchasedStock auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPurchasedStockUpdatableFieldsEquals(PurchasedStock expected, PurchasedStock actual) {
        assertThat(expected)
            .as("Verify PurchasedStock relevant properties")
            .satisfies(e -> assertThat(e.getSupplie()).as("check supplie").isEqualTo(actual.getSupplie()))
            .satisfies(e -> assertThat(e.getUnitPrice()).as("check unitPrice").isEqualTo(actual.getUnitPrice()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPurchasedStockUpdatableRelationshipsEquals(PurchasedStock expected, PurchasedStock actual) {
        assertThat(expected)
            .as("Verify PurchasedStock relationships")
            .satisfies(e -> assertThat(e.getPurchasedWare()).as("check purchasedWare").isEqualTo(actual.getPurchasedWare()))
            .satisfies(e -> assertThat(e.getPurchase()).as("check purchase").isEqualTo(actual.getPurchase()));
    }
}
