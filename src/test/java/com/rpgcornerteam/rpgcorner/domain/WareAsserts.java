package com.rpgcornerteam.rpgcorner.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class WareAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertWareAllPropertiesEquals(Ware expected, Ware actual) {
        assertWareAutoGeneratedPropertiesEquals(expected, actual);
        assertWareAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertWareAllUpdatablePropertiesEquals(Ware expected, Ware actual) {
        assertWareUpdatableFieldsEquals(expected, actual);
        assertWareUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertWareAutoGeneratedPropertiesEquals(Ware expected, Ware actual) {
        assertThat(expected)
            .as("Verify Ware auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertWareUpdatableFieldsEquals(Ware expected, Ware actual) {
        assertThat(expected)
            .as("Verify Ware relevant properties")
            .satisfies(e -> assertThat(e.getActive()).as("check active").isEqualTo(actual.getActive()))
            .satisfies(e -> assertThat(e.getCreated()).as("check created").isEqualTo(actual.getCreated()))
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()))
            .satisfies(e -> assertThat(e.getProductCode()).as("check productCode").isEqualTo(actual.getProductCode()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertWareUpdatableRelationshipsEquals(Ware expected, Ware actual) {
        assertThat(expected)
            .as("Verify Ware relationships")
            .satisfies(e -> assertThat(e.getMainCategory()).as("check mainCategory").isEqualTo(actual.getMainCategory()))
            .satisfies(e -> assertThat(e.getSubCategory()).as("check subCategory").isEqualTo(actual.getSubCategory()));
    }
}
