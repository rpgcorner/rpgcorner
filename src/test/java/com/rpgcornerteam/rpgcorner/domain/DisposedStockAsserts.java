package com.rpgcornerteam.rpgcorner.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class DisposedStockAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDisposedStockAllPropertiesEquals(DisposedStock expected, DisposedStock actual) {
        assertDisposedStockAutoGeneratedPropertiesEquals(expected, actual);
        assertDisposedStockAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDisposedStockAllUpdatablePropertiesEquals(DisposedStock expected, DisposedStock actual) {
        assertDisposedStockUpdatableFieldsEquals(expected, actual);
        assertDisposedStockUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDisposedStockAutoGeneratedPropertiesEquals(DisposedStock expected, DisposedStock actual) {
        assertThat(expected)
            .as("Verify DisposedStock auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDisposedStockUpdatableFieldsEquals(DisposedStock expected, DisposedStock actual) {
        assertThat(expected)
            .as("Verify DisposedStock relevant properties")
            .satisfies(e -> assertThat(e.getSupplie()).as("check supplie").isEqualTo(actual.getSupplie()))
            .satisfies(e -> assertThat(e.getUnitPrice()).as("check unitPrice").isEqualTo(actual.getUnitPrice()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertDisposedStockUpdatableRelationshipsEquals(DisposedStock expected, DisposedStock actual) {
        assertThat(expected)
            .as("Verify DisposedStock relationships")
            .satisfies(e -> assertThat(e.getDisposedWare()).as("check disposedWare").isEqualTo(actual.getDisposedWare()))
            .satisfies(e -> assertThat(e.getDispose()).as("check dispose").isEqualTo(actual.getDispose()));
    }
}
