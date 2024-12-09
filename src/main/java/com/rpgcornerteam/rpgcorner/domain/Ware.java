package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Ware entitás.
 * <p>
 * Az árucikkek entitása.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A Ware entitás.\n<p>\nAz árucikkek entitása.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "ware")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ware implements Serializable {

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
     * Az árucikk bejegyzésének dátuma.
     */
    @Schema(description = "Az árucikk bejegyzésének dátuma.")
    @Column(name = "created")
    private LocalDate created;

    /**
     * Az árucikk megnevezése.
     */
    @Schema(description = "Az árucikk megnevezése.")
    @Column(name = "name")
    private String name;

    /**
     * Az árucikk leírása.
     */
    @Schema(description = "Az árucikk leírása.")
    @Column(name = "description")
    private String description;

    /**
     * Gyártási azonosító kód
     */
    @Schema(description = "Gyártási azonosító kód")
    @Column(name = "product_code")
    private String productCode;

    /**
     * Az árucikk fő kategóriája.
     */
    @Schema(description = "Az árucikk fő kategóriája.")
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "subCategories", "mainCategory" }, allowSetters = true)
    private Category mainCategory;

    /**
     * Az árucikk alkategóriája.
     */
    @Schema(description = "Az árucikk alkategóriája.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "subCategories", "mainCategory" }, allowSetters = true)
    private Category subCategory;

    /**
     * Az árucikk raktárkészlete.
     */
    @Schema(description = "Az árucikk raktárkészlete.")
    @JsonIgnoreProperties(value = { "ware" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "ware")
    private Inventory inventory;

    @JsonIgnoreProperties(value = { "disposedWare", "dispose" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "disposedWare")
    private DisposedStock disposedStock;

    @JsonIgnoreProperties(value = { "purchasedWare", "purchase" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "purchasedWare")
    private PurchasedStock purchasedStock;

    @JsonIgnoreProperties(value = { "returnedWare", "productReturn" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "returnedWare")
    private ReturnedStock returnedStock;

    @JsonIgnoreProperties(value = { "soldWare", "sale" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "soldWare")
    private SoldStock soldStock;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ware id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Ware active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getCreated() {
        return this.created;
    }

    public Ware created(LocalDate created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public String getName() {
        return this.name;
    }

    public Ware name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Ware description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCode() {
        return this.productCode;
    }

    public Ware productCode(String productCode) {
        this.setProductCode(productCode);
        return this;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Category getMainCategory() {
        return this.mainCategory;
    }

    public void setMainCategory(Category category) {
        this.mainCategory = category;
    }

    public Ware mainCategory(Category category) {
        this.setMainCategory(category);
        return this;
    }

    public Category getSubCategory() {
        return this.subCategory;
    }

    public void setSubCategory(Category category) {
        this.subCategory = category;
    }

    public Ware subCategory(Category category) {
        this.setSubCategory(category);
        return this;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        if (this.inventory != null) {
            this.inventory.setWare(null);
        }
        if (inventory != null) {
            inventory.setWare(this);
        }
        this.inventory = inventory;
    }

    public Ware inventory(Inventory inventory) {
        this.setInventory(inventory);
        return this;
    }

    public DisposedStock getDisposedStock() {
        return this.disposedStock;
    }

    public void setDisposedStock(DisposedStock disposedStock) {
        if (this.disposedStock != null) {
            this.disposedStock.setDisposedWare(null);
        }
        if (disposedStock != null) {
            disposedStock.setDisposedWare(this);
        }
        this.disposedStock = disposedStock;
    }

    public Ware disposedStock(DisposedStock disposedStock) {
        this.setDisposedStock(disposedStock);
        return this;
    }

    public PurchasedStock getPurchasedStock() {
        return this.purchasedStock;
    }

    public void setPurchasedStock(PurchasedStock purchasedStock) {
        if (this.purchasedStock != null) {
            this.purchasedStock.setPurchasedWare(null);
        }
        if (purchasedStock != null) {
            purchasedStock.setPurchasedWare(this);
        }
        this.purchasedStock = purchasedStock;
    }

    public Ware purchasedStock(PurchasedStock purchasedStock) {
        this.setPurchasedStock(purchasedStock);
        return this;
    }

    public ReturnedStock getReturnedStock() {
        return this.returnedStock;
    }

    public void setReturnedStock(ReturnedStock returnedStock) {
        if (this.returnedStock != null) {
            this.returnedStock.setReturnedWare(null);
        }
        if (returnedStock != null) {
            returnedStock.setReturnedWare(this);
        }
        this.returnedStock = returnedStock;
    }

    public Ware returnedStock(ReturnedStock returnedStock) {
        this.setReturnedStock(returnedStock);
        return this;
    }

    public SoldStock getSoldStock() {
        return this.soldStock;
    }

    public void setSoldStock(SoldStock soldStock) {
        if (this.soldStock != null) {
            this.soldStock.setSoldWare(null);
        }
        if (soldStock != null) {
            soldStock.setSoldWare(this);
        }
        this.soldStock = soldStock;
    }

    public Ware soldStock(SoldStock soldStock) {
        this.setSoldStock(soldStock);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ware)) {
            return false;
        }
        return getId() != null && getId().equals(((Ware) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ware{" +
            "id=" + getId() +
            ", active='" + getActive() + "'" +
            ", created='" + getCreated() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", productCode='" + getProductCode() + "'" +
            "}";
    }
}
