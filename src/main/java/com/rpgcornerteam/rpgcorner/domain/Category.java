package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rpgcornerteam.rpgcorner.domain.enumeration.CategoryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Category entitás.
 * <p>
 * Az árucikkek kategóriáját kezelő entitása.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A Category entitás.\n<p>\nAz árucikkek kategóriáját kezelő entitása.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "category")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Az elem aktív státuszát jelzi.
     */
    @Schema(description = "Az elem aktív státuszát jelzi.")
    @Column(name = "active")
    private Boolean active;

    /**
     * A kategória típusa.
     */
    @Schema(description = "A kategória típusa.")
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryTypeEnum categoryType;

    /**
     * A kategória leírása.
     */
    @Schema(description = "A kategória leírása.")
    @Column(name = "description")
    private String description;

    /**
     * Alkategóriák.
     */
    @Schema(description = "Alkategóriák.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mainCategory")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "subCategories", "mainCategory" }, allowSetters = true)
    private Set<Category> subCategories = new HashSet<>();

    /**
     * Fő kategória.
     */
    @Schema(description = "Fő kategória.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "subCategories", "mainCategory" }, allowSetters = true)
    private Category mainCategory;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Category active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public CategoryTypeEnum getCategoryType() {
        return this.categoryType;
    }

    public Category categoryType(CategoryTypeEnum categoryType) {
        this.setCategoryType(categoryType);
        return this;
    }

    public void setCategoryType(CategoryTypeEnum categoryType) {
        this.categoryType = categoryType;
    }

    public String getDescription() {
        return this.description;
    }

    public Category description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Category> getSubCategories() {
        return this.subCategories;
    }

    public void setSubCategories(Set<Category> categories) {
        if (this.subCategories != null) {
            this.subCategories.forEach(i -> i.setMainCategory(null));
        }
        if (categories != null) {
            categories.forEach(i -> i.setMainCategory(this));
        }
        this.subCategories = categories;
    }

    public Category subCategories(Set<Category> categories) {
        this.setSubCategories(categories);
        return this;
    }

    public Category addSubCategory(Category category) {
        this.subCategories.add(category);
        category.setMainCategory(this);
        return this;
    }

    public Category removeSubCategory(Category category) {
        this.subCategories.remove(category);
        category.setMainCategory(null);
        return this;
    }

    public Category getMainCategory() {
        return this.mainCategory;
    }

    public void setMainCategory(Category category) {
        this.mainCategory = category;
    }

    public Category mainCategory(Category category) {
        this.setMainCategory(category);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return getId() != null && getId().equals(((Category) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", active='" + getActive() + "'" +
            ", categoryType='" + getCategoryType() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
