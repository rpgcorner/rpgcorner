package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Customer entitás.
 * <p>
 * A vevő adatait kezelő entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A Customer entitás.\n<p>\nA vevő adatait kezelő entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A vevő kapcsolattartási adatai.
     */
    @Schema(description = "A vevő kapcsolattartási adatai.")
    @JsonIgnoreProperties(value = { "supplier", "customer" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Contact contact;

    /**
     * A vevő vásárlásai.
     */
    @Schema(description = "A vevő vásárlásai.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "soldForCustomer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "soldStocks", "soldByUser", "soldForCustomer", "productReturns" }, allowSetters = true)
    private Set<Sale> sales = new HashSet<>();

    /**
     * A vevőhöz kapcsolódó áruvisszavételek.
     */
    @Schema(description = "A vevőhöz kapcsolódó áruvisszavételek.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "returnedByCustomer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "returnedStocks", "sale", "returnedByUser", "returnedByCustomer" }, allowSetters = true)
    private Set<ProductReturn> productReturns = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact getContact() {
        return this.contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Customer contact(Contact contact) {
        this.setContact(contact);
        return this;
    }

    public Set<Sale> getSales() {
        return this.sales;
    }

    public void setSales(Set<Sale> sales) {
        if (this.sales != null) {
            this.sales.forEach(i -> i.setSoldForCustomer(null));
        }
        if (sales != null) {
            sales.forEach(i -> i.setSoldForCustomer(this));
        }
        this.sales = sales;
    }

    public Customer sales(Set<Sale> sales) {
        this.setSales(sales);
        return this;
    }

    public Customer addSale(Sale sale) {
        this.sales.add(sale);
        sale.setSoldForCustomer(this);
        return this;
    }

    public Customer removeSale(Sale sale) {
        this.sales.remove(sale);
        sale.setSoldForCustomer(null);
        return this;
    }

    public Set<ProductReturn> getProductReturns() {
        return this.productReturns;
    }

    public void setProductReturns(Set<ProductReturn> productReturns) {
        if (this.productReturns != null) {
            this.productReturns.forEach(i -> i.setReturnedByCustomer(null));
        }
        if (productReturns != null) {
            productReturns.forEach(i -> i.setReturnedByCustomer(this));
        }
        this.productReturns = productReturns;
    }

    public Customer productReturns(Set<ProductReturn> productReturns) {
        this.setProductReturns(productReturns);
        return this;
    }

    public Customer addProductReturn(ProductReturn productReturn) {
        this.productReturns.add(productReturn);
        productReturn.setReturnedByCustomer(this);
        return this;
    }

    public Customer removeProductReturn(ProductReturn productReturn) {
        this.productReturns.remove(productReturn);
        productReturn.setReturnedByCustomer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return getId() != null && getId().equals(((Customer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            "}";
    }
}
