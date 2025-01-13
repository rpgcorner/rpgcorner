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
 * A Sale entitás.
 * <p>
 * Az eladás entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A Sale entitás.\n<p>\nAz eladás entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "sale")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Az eladás időpontja.
     */
    @Schema(description = "Az eladás időpontja.")
    @Column(name = "sold_date")
    private LocalDate soldDate;

    /**
     * Az eladott árukészlet.
     */
    @Schema(description = "Az eladott árukészlet.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sale")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "soldWare", "sale" }, allowSetters = true)
    private Set<SoldStock> soldStocks = new HashSet<>();

    /**
     * Az eladó.
     */
    @Schema(description = "Az eladó.")
    @ManyToOne(optional = false)
    @NotNull
    private User soldByUser;

    /**
     * A vevő.
     */
    @Schema(description = "A vevő.")
    @ManyToOne(optional = false)
    @NotNull
    //@JsonIgnoreProperties(value = { "contact", "sales", "productReturns" }, allowSetters = true)
    private Customer soldForCustomer;

    /**
     * Az eladáshoz kapcsolódó áruvisszavételek.
     */
    @Schema(description = "Az eladáshoz kapcsolódó áruvisszavételek.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sale")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "returnedStocks", "sale", "returnedByUser", "returnedByCustomer" }, allowSetters = true)
    private Set<ProductReturn> productReturns = new HashSet<>();

    @Column(name = "transactionClosed")
    private boolean transactionClosed;

    public boolean getTransactionClosed() {
        return this.transactionClosed;
    }

    public void setTransactionClosed(boolean transactionClosed) {
        this.transactionClosed = transactionClosed;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sale id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSoldDate() {
        return this.soldDate;
    }

    public Sale soldDate(LocalDate soldDate) {
        this.setSoldDate(soldDate);
        return this;
    }

    public void setSoldDate(LocalDate soldDate) {
        this.soldDate = soldDate;
    }

    public Set<SoldStock> getSoldStocks() {
        return this.soldStocks;
    }

    public void setSoldStocks(Set<SoldStock> soldStocks) {
        if (this.soldStocks != null) {
            this.soldStocks.forEach(i -> i.setSale(null));
        }
        if (soldStocks != null) {
            soldStocks.forEach(i -> i.setSale(this));
        }
        this.soldStocks = soldStocks;
    }

    public Sale soldStocks(Set<SoldStock> soldStocks) {
        this.setSoldStocks(soldStocks);
        return this;
    }

    public Sale addSoldStock(SoldStock soldStock) {
        this.soldStocks.add(soldStock);
        soldStock.setSale(this);
        return this;
    }

    public Sale removeSoldStock(SoldStock soldStock) {
        this.soldStocks.remove(soldStock);
        soldStock.setSale(null);
        return this;
    }

    public User getSoldByUser() {
        return this.soldByUser;
    }

    public void setSoldByUser(User user) {
        this.soldByUser = user;
    }

    public Sale soldByUser(User user) {
        this.setSoldByUser(user);
        return this;
    }

    public Customer getSoldForCustomer() {
        return this.soldForCustomer;
    }

    public void setSoldForCustomer(Customer customer) {
        this.soldForCustomer = customer;
    }

    public Sale soldForCustomer(Customer customer) {
        this.setSoldForCustomer(customer);
        return this;
    }

    public Set<ProductReturn> getProductReturns() {
        return this.productReturns;
    }

    public void setProductReturns(Set<ProductReturn> productReturns) {
        if (this.productReturns != null) {
            this.productReturns.forEach(i -> i.setSale(null));
        }
        if (productReturns != null) {
            productReturns.forEach(i -> i.setSale(this));
        }
        this.productReturns = productReturns;
    }

    public Sale productReturns(Set<ProductReturn> productReturns) {
        this.setProductReturns(productReturns);
        return this;
    }

    public Sale addProductReturn(ProductReturn productReturn) {
        this.productReturns.add(productReturn);
        productReturn.setSale(this);
        return this;
    }

    public Sale removeProductReturn(ProductReturn productReturn) {
        this.productReturns.remove(productReturn);
        productReturn.setSale(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sale)) {
            return false;
        }
        return getId() != null && getId().equals(((Sale) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sale{" +
            "id=" + getId() +
            ", soldDate='" + getSoldDate() + "'" +
            "}";
    }
}
