package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SoldStock entitás.
 * <p>
 * Az eladott áru adatait kezelő entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A SoldStock entitás.\n<p>\nAz eladott áru adatait kezelő entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "sold_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SoldStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Az eladott áru mennyisége.
     */
    @Schema(description = "Az eladott áru mennyisége.")
    @Column(name = "supplie")
    private Integer supplie;

    /**
     * Eladási egységár.
     * Eltérhet a beszerzési egységártól.
     */
    @Schema(description = "Eladási egységár.\nEltérhet a beszerzési egységártól.")
    @Column(name = "unit_price")
    private Integer unitPrice;

    /**
     * A visszahozott árumennyiség.
     * Alapértelmezetten 0. Maximum a supplie értéke.
     */
    @Schema(description = "A visszahozott árumennyiség.\nAlapértelmezetten 0. Maximum a supplie értéke.")
    @Column(name = "returned_supplie")
    private Integer returnedSupplie;

    /**
     * Az eladott árucikk.
     */
    @Schema(description = "Az eladott árucikk.")
    @JsonIgnoreProperties(
        value = { "mainCategory", "subCategory", "inventory", "disposedStock", "purchasedStock", "returnedStock", "soldStock" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Ware soldWare;

    /**
     * Az eladás, amely során az árukészlet eladásra került.
     */
    @Schema(description = "Az eladás, amely során az árukészlet eladásra került.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "soldStocks", "soldByUser", "soldForCustomer", "productReturns" }, allowSetters = true)
    private Sale sale;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SoldStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupplie() {
        return this.supplie;
    }

    public SoldStock supplie(Integer supplie) {
        this.setSupplie(supplie);
        return this;
    }

    public void setSupplie(Integer supplie) {
        this.supplie = supplie;
    }

    public Integer getUnitPrice() {
        return this.unitPrice;
    }

    public SoldStock unitPrice(Integer unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getReturnedSupplie() {
        return this.returnedSupplie;
    }

    public SoldStock returnedSupplie(Integer returnedSupplie) {
        this.setReturnedSupplie(returnedSupplie);
        return this;
    }

    public void setReturnedSupplie(Integer returnedSupplie) {
        this.returnedSupplie = returnedSupplie;
    }

    public Ware getSoldWare() {
        return this.soldWare;
    }

    public void setSoldWare(Ware ware) {
        this.soldWare = ware;
    }

    public SoldStock soldWare(Ware ware) {
        this.setSoldWare(ware);
        return this;
    }

    public Sale getSale() {
        return this.sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public SoldStock sale(Sale sale) {
        this.setSale(sale);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SoldStock)) {
            return false;
        }
        return getId() != null && getId().equals(((SoldStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SoldStock{" +
            "id=" + getId() +
            ", supplie=" + getSupplie() +
            ", unitPrice=" + getUnitPrice() +
            ", returnedSupplie=" + getReturnedSupplie() +
            "}";
    }
}
