package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Purchase entitás.
 * <p>
 * A beszerzés entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A Purchase entitás.\n<p>\nA beszerzés entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "purchase")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Purchase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A beszerzés időpontja.
     */
    @Schema(description = "A beszerzés időpontja.")
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    /**
     * A bevételezett árukészlet.
     */
    @Schema(description = "A bevételezett árukészlet.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchase")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "purchasedWare", "purchase" }, allowSetters = true)
    private Set<PurchasedStock> purchasedStocks = new HashSet<>();

    /**
     * Az árubeszerző.
     */
    @Schema(description = "Az árubeszerző.")
    @ManyToOne(optional = false)
    @NotNull
    private User purchasedByUser;

    /**
     * A beszállító.
     */
    @Schema(description = "A beszállító.")
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "contacts", "purchases" }, allowSetters = true)
    private Supplier purchasedFromSupplier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Purchase id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPurchaseDate() {
        return this.purchaseDate;
    }

    public Purchase purchaseDate(LocalDate purchaseDate) {
        this.setPurchaseDate(purchaseDate);
        return this;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Set<PurchasedStock> getPurchasedStocks() {
        return this.purchasedStocks;
    }

    public void setPurchasedStocks(Set<PurchasedStock> purchasedStocks) {
        if (this.purchasedStocks != null) {
            this.purchasedStocks.forEach(i -> i.setPurchase(null));
        }
        if (purchasedStocks != null) {
            purchasedStocks.forEach(i -> i.setPurchase(this));
        }
        this.purchasedStocks = purchasedStocks;
    }

    public Purchase purchasedStocks(Set<PurchasedStock> purchasedStocks) {
        this.setPurchasedStocks(purchasedStocks);
        return this;
    }

    public Purchase addPurchasedStock(PurchasedStock purchasedStock) {
        this.purchasedStocks.add(purchasedStock);
        purchasedStock.setPurchase(this);
        return this;
    }

    public Purchase removePurchasedStock(PurchasedStock purchasedStock) {
        this.purchasedStocks.remove(purchasedStock);
        purchasedStock.setPurchase(null);
        return this;
    }

    public User getPurchasedByUser() {
        return this.purchasedByUser;
    }

    public void setPurchasedByUser(User user) {
        this.purchasedByUser = user;
    }

    public Purchase purchasedByUser(User user) {
        this.setPurchasedByUser(user);
        return this;
    }

    public Supplier getPurchasedFromSupplier() {
        return this.purchasedFromSupplier;
    }

    public void setPurchasedFromSupplier(Supplier supplier) {
        this.purchasedFromSupplier = supplier;
    }

    public Purchase purchasedFromSupplier(Supplier supplier) {
        this.setPurchasedFromSupplier(supplier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Purchase)) {
            return false;
        }
        return getId() != null && getId().equals(((Purchase) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Purchase{" +
            "id=" + getId() +
            ", purchaseDate='" + getPurchaseDate() + "'" +
            "}";
    }
}
