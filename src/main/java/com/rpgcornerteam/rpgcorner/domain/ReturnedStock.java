package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ReturnedStock entitás.
 * <p>
 * A visszavett árucikk adatait kezelő entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A ReturnedStock entitás.\n<p>\nA visszavett árucikk adatait kezelő entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "returned_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnedStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A visszahozott áru mennyisége.
     */
    @Schema(description = "A visszahozott áru mennyisége.")
    @Column(name = "supplie")
    private Integer supplie;

    /**
     * A visszavételi egységár.
     * Eltérhet az eladási egységártól.
     */
    @Schema(description = "A visszavételi egységár.\nEltérhet az eladási egységártól.")
    @Column(name = "unit_price")
    private Integer unitPrice;

    /**
     * A visszahozott árucikk
     */
    @Schema(description = "A visszahozott árucikk")
    @JsonIgnoreProperties(
        value = { "mainCategory", "subCategory", "inventory", "disposedStock", "purchasedStock", "returnedStock", "soldStock" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Ware returnedWare;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "returnedStocks", "sale", "returnedByUser", "returnedByCustomer" }, allowSetters = true)
    private ProductReturn productReturn;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReturnedStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupplie() {
        return this.supplie;
    }

    public ReturnedStock supplie(Integer supplie) {
        this.setSupplie(supplie);
        return this;
    }

    public void setSupplie(Integer supplie) {
        this.supplie = supplie;
    }

    public Integer getUnitPrice() {
        return this.unitPrice;
    }

    public ReturnedStock unitPrice(Integer unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Ware getReturnedWare() {
        return this.returnedWare;
    }

    public void setReturnedWare(Ware ware) {
        this.returnedWare = ware;
    }

    public ReturnedStock returnedWare(Ware ware) {
        this.setReturnedWare(ware);
        return this;
    }

    public ProductReturn getProductReturn() {
        return this.productReturn;
    }

    public void setProductReturn(ProductReturn productReturn) {
        this.productReturn = productReturn;
    }

    public ReturnedStock productReturn(ProductReturn productReturn) {
        this.setProductReturn(productReturn);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnedStock)) {
            return false;
        }
        return getId() != null && getId().equals(((ReturnedStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnedStock{" +
            "id=" + getId() +
            ", supplie=" + getSupplie() +
            ", unitPrice=" + getUnitPrice() +
            "}";
    }
}
