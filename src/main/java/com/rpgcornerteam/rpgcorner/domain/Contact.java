package com.rpgcornerteam.rpgcorner.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Contact entitás.
 * <p>
 * A kapcsolattartási információk entitása.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A Contact entitás.\n<p>\nA kapcsolattartási információk entitása.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "contact")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A kapcsolat neve.
     */
    @Schema(description = "A kapcsolat neve.")
    @Column(name = "contact_name")
    private String contactName;

    /**
     * Cím.
     */
    @Schema(description = "Cím.")
    @Column(name = "address")
    private String address;

    /**
     * E-mail cím.
     */
    @Schema(description = "E-mail cím.")
    @Column(name = "email")
    private String email;

    /**
     * Fax szám.
     */
    @Schema(description = "Fax szám.")
    @Column(name = "fax")
    private String fax;

    /**
     * Mobil szám.
     */
    @Schema(description = "Mobil szám.")
    @Column(name = "mobile")
    private String mobile;

    /**
     * Vezetékes telefonszám.
     */
    @Schema(description = "Vezetékes telefonszám.")
    @Column(name = "phone")
    private String phone;

    /**
     * Megjegyzés
     */
    @Schema(description = "Megjegyzés")
    @Column(name = "note")
    private String note;

    /**
     * A beszállító amelyhez a kapcsolattartási adatok tartoznak.
     */
    @Schema(description = "A beszállító amelyhez a kapcsolattartási adatok tartoznak.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "contacts", "purchases" }, allowSetters = true)
    private Supplier supplier;

    @JsonIgnoreProperties(value = { "contact", "sales", "productReturns" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "contact")
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Contact id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContactName() {
        return this.contactName;
    }

    public Contact contactName(String contactName) {
        this.setContactName(contactName);
        return this;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getAddress() {
        return this.address;
    }

    public Contact address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return this.email;
    }

    public Contact email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return this.fax;
    }

    public Contact fax(String fax) {
        this.setFax(fax);
        return this;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMobile() {
        return this.mobile;
    }

    public Contact mobile(String mobile) {
        this.setMobile(mobile);
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return this.phone;
    }

    public Contact phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return this.note;
    }

    public Contact note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Contact supplier(Supplier supplier) {
        this.setSupplier(supplier);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            this.customer.setContact(null);
        }
        if (customer != null) {
            customer.setContact(this);
        }
        this.customer = customer;
    }

    public Contact customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contact)) {
            return false;
        }
        return getId() != null && getId().equals(((Contact) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Contact{" +
            "id=" + getId() +
            ", contactName='" + getContactName() + "'" +
            ", address='" + getAddress() + "'" +
            ", email='" + getEmail() + "'" +
            ", fax='" + getFax() + "'" +
            ", mobile='" + getMobile() + "'" +
            ", phone='" + getPhone() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
