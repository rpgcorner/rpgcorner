package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PurchasedStock entitás.
 * <p>
 * A beszerzett áru adatait kezelő entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A PurchasedStock entitás.\n<p>\nA beszerzett áru adatait kezelő entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "purchased_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PurchasedStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A beszerzett áru mennyisége.
     */
    @Schema(description = "A beszerzett áru mennyisége.")
    @Column(name = "supplie")
    private Integer supplie;

    /**
     * Beszerzési egység ár
     */
    @Schema(description = "Beszerzési egység ár")
    @Column(name = "unit_price")
    private Integer unitPrice;

    /**
     * A beszerzett árucikk.
     */
    @Schema(description = "A beszerzett árucikk.")
    @JsonIgnoreProperties(
        value = { "mainCategory", "subCategory", "inventory", "disposedStock", "purchasedStock", "returnedStock", "soldStock" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Ware purchasedWare;

    /**
     * A beszerzés, amelyhez az árukészlet kapcsolódik.
     */
    @Schema(description = "A beszerzés, amelyhez az árukészlet kapcsolódik.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "purchasedStocks", "purchasedByUser", "purchasedFromSupplier" }, allowSetters = true)
    private Purchase purchase;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PurchasedStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupplie() {
        return this.supplie;
    }

    public PurchasedStock supplie(Integer supplie) {
        this.setSupplie(supplie);
        return this;
    }

    public void setSupplie(Integer supplie) {
        this.supplie = supplie;
    }

    public Integer getUnitPrice() {
        return this.unitPrice;
    }

    public PurchasedStock unitPrice(Integer unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Ware getPurchasedWare() {
        return this.purchasedWare;
    }

    public void setPurchasedWare(Ware ware) {
        this.purchasedWare = ware;
    }

    public PurchasedStock purchasedWare(Ware ware) {
        this.setPurchasedWare(ware);
        return this;
    }

    public Purchase getPurchase() {
        return this.purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public PurchasedStock purchase(Purchase purchase) {
        this.setPurchase(purchase);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchasedStock)) {
            return false;
        }
        return getId() != null && getId().equals(((PurchasedStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchasedStock{" +
            "id=" + getId() +
            ", supplie=" + getSupplie() +
            ", unitPrice=" + getUnitPrice() +
            "}";
    }
}
