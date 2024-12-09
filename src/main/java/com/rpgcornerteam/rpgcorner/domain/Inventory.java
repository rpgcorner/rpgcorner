package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Az Inventory entitás.
 * <p>
 * A raktárkészletet nyilvántartó entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "Az Inventory entitás.\n<p>\nA raktárkészletet nyilvántartó entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "inventory")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A raktáron lévő áru mennyisége.
     */
    @Schema(description = "A raktáron lévő áru mennyisége.")
    @Column(name = "supplie")
    private Integer supplie;

    /**
     * A raktáron lévő árucikk.
     */
    @Schema(description = "A raktáron lévő árucikk.")
    @JsonIgnoreProperties(
        value = { "mainCategory", "subCategory", "inventory", "disposedStock", "purchasedStock", "returnedStock", "soldStock" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Ware ware;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Inventory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupplie() {
        return this.supplie;
    }

    public Inventory supplie(Integer supplie) {
        this.setSupplie(supplie);
        return this;
    }

    public void setSupplie(Integer supplie) {
        this.supplie = supplie;
    }

    public Ware getWare() {
        return this.ware;
    }

    public void setWare(Ware ware) {
        this.ware = ware;
    }

    public Inventory ware(Ware ware) {
        this.setWare(ware);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inventory)) {
            return false;
        }
        return getId() != null && getId().equals(((Inventory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inventory{" +
            "id=" + getId() +
            ", supplie=" + getSupplie() +
            "}";
    }
}
