package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.Contact;
import com.rpgcornerteam.rpgcorner.domain.Supplier;
import com.rpgcornerteam.rpgcorner.repository.SupplierRepository;
import com.rpgcornerteam.rpgcorner.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.Supplier}.
 */
@RestController
@RequestMapping("/api/suppliers")
@Transactional
public class SupplierResource {

    private static final Logger LOG = LoggerFactory.getLogger(SupplierResource.class);

    private static final String ENTITY_NAME = "supplier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupplierRepository supplierRepository;

    public SupplierResource(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    /**
     * {@code POST  /suppliers} : Create a new supplier.
     *
     * @param supplier the supplier to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supplier, or with status {@code 400 (Bad Request)} if the supplier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody Supplier supplier) throws URISyntaxException {
        LOG.debug("REST request to save Supplier : {}", supplier);
        if (supplier.getId() != null) {
            throw new BadRequestAlertException("A new supplier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        supplier = supplierRepository.save(supplier);
        return ResponseEntity.created(new URI("/api/suppliers/" + supplier.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, supplier.getId().toString()))
            .body(supplier);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Supplier> partialUpdateContact(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Supplier supplier
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Contact supplier : {}, {}", id, supplier);
        if (supplier.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplier.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Supplier> result = supplierRepository
            .findById(supplier.getId())
            .map(existingContact -> {
                if (supplier.getCompanyName() != null) {
                    existingContact.setCompanyName(supplier.getCompanyName());
                }
                if (supplier.getTaxNumber() != null) {
                    existingContact.setTaxNumber(supplier.getTaxNumber());
                }

                return existingContact;
            })
            .map(supplierRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplier.getId().toString())
        );
    }

    /**
     * {@code GET  /suppliers} : get all the suppliers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of suppliers in body.
     */
    @GetMapping("")
    public List<Supplier> getAllSuppliers() {
        LOG.debug("REST request to get all Suppliers");
        return supplierRepository.findAll();
    }

    /**
     * {@code GET  /suppliers/:id} : get the "id" supplier.
     *
     * @param id the id of the supplier to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supplier, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplier(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Supplier : {}", id);
        Optional<Supplier> supplier = supplierRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(supplier);
    }

    /**
     * {@code DELETE  /suppliers/:id} : delete the "id" supplier.
     *
     * @param id the id of the supplier to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Supplier : {}", id);
        supplierRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/search")
    public List<Supplier> getAllSuppliers(@RequestParam(required = false) String searchParam) {
        LOG.debug("REST request to get all Suppliers");
        if (searchParam != null && !searchParam.isEmpty()) {
            LOG.debug("Filtering suppliers by searchParam: {}", searchParam);
            return supplierRepository.findByCompanyNameContainingIgnoreCase(searchParam);
        }
        return supplierRepository.findAll();
    }
}
