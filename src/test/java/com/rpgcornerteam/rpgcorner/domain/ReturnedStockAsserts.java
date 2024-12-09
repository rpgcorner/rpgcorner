package com.rpgcornerteam.rpgcorner.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ReturnedStockAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReturnedStockAllPropertiesEquals(ReturnedStock expected, ReturnedStock actual) {
        assertReturnedStockAutoGeneratedPropertiesEquals(expected, actual);
        assertReturnedStockAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReturnedStockAllUpdatablePropertiesEquals(ReturnedStock expected, ReturnedStock actual) {
        assertReturnedStockUpdatableFieldsEquals(expected, actual);
        assertReturnedStockUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReturnedStockAutoGeneratedPropertiesEquals(ReturnedStock expected, ReturnedStock actual) {
        assertThat(expected)
            .as("Verify ReturnedStock auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReturnedStockUpdatableFieldsEquals(ReturnedStock expected, ReturnedStock actual) {
        assertThat(expected)
            .as("Verify ReturnedStock relevant properties")
            .satisfies(e -> assertThat(e.getSupplie()).as("check supplie").isEqualTo(actual.getSupplie()))
            .satisfies(e -> assertThat(e.getUnitPrice()).as("check unitPrice").isEqualTo(actual.getUnitPrice()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReturnedStockUpdatableRelationshipsEquals(ReturnedStock expected, ReturnedStock actual) {
        assertThat(expected)
            .as("Verify ReturnedStock relationships")
            .satisfies(e -> assertThat(e.getReturnedWare()).as("check returnedWare").isEqualTo(actual.getReturnedWare()))
            .satisfies(e -> assertThat(e.getProductReturn()).as("check productReturn").isEqualTo(actual.getProductReturn()));
    }
}
