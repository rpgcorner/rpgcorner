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
 * A Dispose entitás.
 * <p>
 * A selejtezés entitás.
 *
 * @author Kárpáti Gábor
 */
@Schema(description = "A Dispose entitás.\n<p>\nA selejtezés entitás.\n\n@author Kárpáti Gábor")
@Entity
@Table(name = "dispose")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Dispose implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * A selejtezés dátuma.
     */
    @Schema(description = "A selejtezés dátuma.")
    @Column(name = "dispose_date")
    private LocalDate disposeDate;

    /**
     * Megjegyzés.
     */
    @Schema(description = "Megjegyzés.")
    @Column(name = "note")
    private String note;

    /**
     * A selejtezett árukészlet.
     */
    @Schema(description = "A selejtezett árukészlet.")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dispose")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "disposedWare", "dispose" }, allowSetters = true)
    private Set<DisposedStock> disposedStocks = new HashSet<>();

    /**
     * A selejtezést végző felhasználó.
     */
    @Schema(description = "A selejtezést végző felhasználó.")
    @ManyToOne(optional = false)
    @NotNull
    private User disposedByUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @Column(name = "transactionClosed")
    private boolean transactionClosed;

    public boolean getTransactionClosed() {
        return this.transactionClosed;
    }

    public void setTransactionClosed(boolean transactionClosed) {
        this.transactionClosed = transactionClosed;
    }

    public Long getId() {
        return this.id;
    }

    public Dispose id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDisposeDate() {
        return this.disposeDate;
    }

    public Dispose disposeDate(LocalDate disposeDate) {
        this.setDisposeDate(disposeDate);
        return this;
    }

    public void setDisposeDate(LocalDate disposeDate) {
        this.disposeDate = disposeDate;
    }

    public String getNote() {
        return this.note;
    }

    public Dispose note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<DisposedStock> getDisposedStocks() {
        return this.disposedStocks;
    }

    public void setDisposedStocks(Set<DisposedStock> disposedStocks) {
        if (this.disposedStocks != null) {
            this.disposedStocks.forEach(i -> i.setDispose(null));
        }
        if (disposedStocks != null) {
            disposedStocks.forEach(i -> i.setDispose(this));
        }
        this.disposedStocks = disposedStocks;
    }

    public Dispose disposedStocks(Set<DisposedStock> disposedStocks) {
        this.setDisposedStocks(disposedStocks);
        return this;
    }

    public Dispose addDisposedStock(DisposedStock disposedStock) {
        this.disposedStocks.add(disposedStock);
        disposedStock.setDispose(this);
        return this;
    }

    public Dispose removeDisposedStock(DisposedStock disposedStock) {
        this.disposedStocks.remove(disposedStock);
        disposedStock.setDispose(null);
        return this;
    }

    public User getDisposedByUser() {
        return this.disposedByUser;
    }

    public void setDisposedByUser(User user) {
        this.disposedByUser = user;
    }

    public Dispose disposedByUser(User user) {
        this.setDisposedByUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dispose)) {
            return false;
        }
        return getId() != null && getId().equals(((Dispose) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dispose{" +
            "id=" + getId() +
            ", disposeDate='" + getDisposeDate() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
