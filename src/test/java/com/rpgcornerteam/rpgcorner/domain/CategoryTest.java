package com.rpgcornerteam.rpgcorner.domain;

import static com.rpgcornerteam.rpgcorner.domain.CategoryTestSamples.*;
import static com.rpgcornerteam.rpgcorner.domain.CategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.rpgcornerteam.rpgcorner.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void subCategoryTest() {
        Category category = getCategoryRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        category.addSubCategory(categoryBack);
        assertThat(category.getSubCategories()).containsOnly(categoryBack);
        assertThat(categoryBack.getMainCategory()).isEqualTo(category);

        category.removeSubCategory(categoryBack);
        assertThat(category.getSubCategories()).doesNotContain(categoryBack);
        assertThat(categoryBack.getMainCategory()).isNull();

        category.subCategories(new HashSet<>(Set.of(categoryBack)));
        assertThat(category.getSubCategories()).containsOnly(categoryBack);
        assertThat(categoryBack.getMainCategory()).isEqualTo(category);

        category.setSubCategories(new HashSet<>());
        assertThat(category.getSubCategories()).doesNotContain(categoryBack);
        assertThat(categoryBack.getMainCategory()).isNull();
    }

    @Test
    void mainCategoryTest() {
        Category category = getCategoryRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        category.setMainCategory(categoryBack);
        assertThat(category.getMainCategory()).isEqualTo(categoryBack);

        category.mainCategory(null);
        assertThat(category.getMainCategory()).isNull();
    }
}
