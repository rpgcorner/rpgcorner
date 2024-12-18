package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Supplier entitás.
 * <p>
 * A beszállító adatait kezelő entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A Supplier entitás.\n<p>\nA beszállító adatait kezelő entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "supplier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A cég neve.
     */
    @Schema(description = "A cég neve.")
    @Column(name = "company_name")
    private String companyName;

    /**
     * Adószám
     */
    @Schema(description = "Adószám")
    @Column(name = "tax_number")
    private String taxNumber;

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    /**
     * A beszállító kapcsolattartói.
     */
    @Schema(description = "A beszállító kapcsolattartói.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "supplier", "customer" }, allowSetters = true)
    private Set<Contact> contacts = new HashSet<>();

    /**
     * A beszállítóhoz kapcsolódó beszerzések.
     */
    @Schema(description = "A beszállítóhoz kapcsolódó beszerzések.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchasedFromSupplier")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "purchasedStocks", "purchasedByUser", "purchasedFromSupplier" }, allowSetters = true)
    private Set<Purchase> purchases = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Supplier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Contact> getContacts() {
        return this.contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        if (this.contacts != null) {
            this.contacts.forEach(i -> i.setSupplier(null));
        }
        if (contacts != null) {
            contacts.forEach(i -> i.setSupplier(this));
        }
        this.contacts = contacts;
    }

    public Supplier contacts(Set<Contact> contacts) {
        this.setContacts(contacts);
        return this;
    }

    public Supplier addContact(Contact contact) {
        this.contacts.add(contact);
        contact.setSupplier(this);
        return this;
    }

    public Supplier removeContact(Contact contact) {
        this.contacts.remove(contact);
        contact.setSupplier(null);
        return this;
    }

    public Set<Purchase> getPurchases() {
        return this.purchases;
    }

    public void setPurchases(Set<Purchase> purchases) {
        if (this.purchases != null) {
            this.purchases.forEach(i -> i.setPurchasedFromSupplier(null));
        }
        if (purchases != null) {
            purchases.forEach(i -> i.setPurchasedFromSupplier(this));
        }
        this.purchases = purchases;
    }

    public Supplier purchases(Set<Purchase> purchases) {
        this.setPurchases(purchases);
        return this;
    }

    public Supplier addPurchase(Purchase purchase) {
        this.purchases.add(purchase);
        purchase.setPurchasedFromSupplier(this);
        return this;
    }

    public Supplier removePurchase(Purchase purchase) {
        this.purchases.remove(purchase);
        purchase.setPurchasedFromSupplier(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Supplier)) {
            return false;
        }
        return getId() != null && getId().equals(((Supplier) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Supplier{" +
            "id=" + getId() +
            "}";
    }
}
