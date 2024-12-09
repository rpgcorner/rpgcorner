package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DisposedStock entitás.
 * <p>
 * A selejtezett áru adatait kezelő entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A DisposedStock entitás.\n<p>\nA selejtezett áru adatait kezelő entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "disposed_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisposedStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A selejtezett áru mennyisége.
     */
    @Schema(description = "A selejtezett áru mennyisége.")
    @Column(name = "supplie")
    private Integer supplie;

    /**
     * Selejtezési egységár.
     * Eltérhet a beszerzési egységártól.
     */
    @Schema(description = "Selejtezési egységár.\nEltérhet a beszerzési egységártól.")
    @Column(name = "unit_price")
    private Integer unitPrice;

    /**
     * A selejtezett árucikk
     */
    @Schema(description = "A selejtezett árucikk")
    @JsonIgnoreProperties(
        value = { "mainCategory", "subCategory", "inventory", "disposedStock", "purchasedStock", "returnedStock", "soldStock" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Ware disposedWare;

    /**
     * A selejtezés, amely során az áruikszélet selejtezésre került.
     */
    @Schema(description = "A selejtezés, amely során az áruikszélet selejtezésre került.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "disposedStocks", "disposedByUser" }, allowSetters = true)
    private Dispose dispose;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DisposedStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupplie() {
        return this.supplie;
    }

    public DisposedStock supplie(Integer supplie) {
        this.setSupplie(supplie);
        return this;
    }

    public void setSupplie(Integer supplie) {
        this.supplie = supplie;
    }

    public Integer getUnitPrice() {
        return this.unitPrice;
    }

    public DisposedStock unitPrice(Integer unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Ware getDisposedWare() {
        return this.disposedWare;
    }

    public void setDisposedWare(Ware ware) {
        this.disposedWare = ware;
    }

    public DisposedStock disposedWare(Ware ware) {
        this.setDisposedWare(ware);
        return this;
    }

    public Dispose getDispose() {
        return this.dispose;
    }

    public void setDispose(Dispose dispose) {
        this.dispose = dispose;
    }

    public DisposedStock dispose(Dispose dispose) {
        this.setDispose(dispose);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisposedStock)) {
            return false;
        }
        return getId() != null && getId().equals(((DisposedStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisposedStock{" +
            "id=" + getId() +
            ", supplie=" + getSupplie() +
            ", unitPrice=" + getUnitPrice() +
            "}";
    }
}
