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
 * A ProductReturn entitás.
 * <p>
 * Az áruvisszavételeket kezelő entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A ProductReturn entitás.\n<p>\nAz áruvisszavételeket kezelő entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "product_return")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductReturn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A visszavétel dátuma
     */
    @Schema(description = "A visszavétel dátuma")
    @Column(name = "return_date")
    private LocalDate returnDate;

    /**
     * Megjegyzés.
     */
    @Schema(description = "Megjegyzés.")
    @Column(name = "note")
    private String note;

    /**
     * A visszavett árukészlet.
     */
    @Schema(description = "A visszavett árukészlet.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productReturn")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "returnedWare", "productReturn" }, allowSetters = true)
    private Set<ReturnedStock> returnedStocks = new HashSet<>();

    /**
     * Az eladás, amelyhez a áruvisszavétel tartozik.
     */
    @Schema(description = "Az eladás, amelyhez a áruvisszavétel tartozik.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "soldStocks", "soldByUser", "soldForCustomer", "productReturns" }, allowSetters = true)
    private Sale sale;

    /**
     * A felhasználó aki az árut visszavette.
     */
    @Schema(description = "A felhasználó aki az árut visszavette.")
    @ManyToOne(fetch = FetchType.LAZY)
    private User returnedByUser;

    /**
     * A vevő aki az árut visszahozta.
     */
    @Schema(description = "A vevő aki az árut visszahozta.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "contact", "sales", "productReturns" }, allowSetters = true)
    private Customer returnedByCustomer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductReturn id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getReturnDate() {
        return this.returnDate;
    }

    public ProductReturn returnDate(LocalDate returnDate) {
        this.setReturnDate(returnDate);
        return this;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getNote() {
        return this.note;
    }

    public ProductReturn note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<ReturnedStock> getReturnedStocks() {
        return this.returnedStocks;
    }

    public void setReturnedStocks(Set<ReturnedStock> returnedStocks) {
        if (this.returnedStocks != null) {
            this.returnedStocks.forEach(i -> i.setProductReturn(null));
        }
        if (returnedStocks != null) {
            returnedStocks.forEach(i -> i.setProductReturn(this));
        }
        this.returnedStocks = returnedStocks;
    }

    public ProductReturn returnedStocks(Set<ReturnedStock> returnedStocks) {
        this.setReturnedStocks(returnedStocks);
        return this;
    }

    public ProductReturn addReturnedStock(ReturnedStock returnedStock) {
        this.returnedStocks.add(returnedStock);
        returnedStock.setProductReturn(this);
        return this;
    }

    public ProductReturn removeReturnedStock(ReturnedStock returnedStock) {
        this.returnedStocks.remove(returnedStock);
        returnedStock.setProductReturn(null);
        return this;
    }

    public Sale getSale() {
        return this.sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public ProductReturn sale(Sale sale) {
        this.setSale(sale);
        return this;
    }

    public User getReturnedByUser() {
        return this.returnedByUser;
    }

    public void setReturnedByUser(User user) {
        this.returnedByUser = user;
    }

    public ProductReturn returnedByUser(User user) {
        this.setReturnedByUser(user);
        return this;
    }

    public Customer getReturnedByCustomer() {
        return this.returnedByCustomer;
    }

    public void setReturnedByCustomer(Customer customer) {
        this.returnedByCustomer = customer;
    }

    public ProductReturn returnedByCustomer(Customer customer) {
        this.setReturnedByCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductReturn)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductReturn) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductReturn{" +
            "id=" + getId() +
            ", returnDate='" + getReturnDate() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
