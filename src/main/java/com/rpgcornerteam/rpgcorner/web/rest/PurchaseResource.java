package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.Purchase;
import com.rpgcornerteam.rpgcorner.repository.PurchaseRepository;
import com.rpgcornerteam.rpgcorner.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.Purchase}.
 */
@RestController
@RequestMapping("/api/purchases")
@Transactional
public class PurchaseResource {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseResource.class);

    private static final String ENTITY_NAME = "purchase";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseRepository purchaseRepository;

    public PurchaseResource(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    /**
     * {@code POST  /purchases} : Create a new purchase.
     *
     * @param purchase the purchase to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchase, or with status {@code 400 (Bad Request)} if the purchase has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Purchase> createPurchase(@Valid @RequestBody Purchase purchase) throws URISyntaxException {
        LOG.debug("REST request to save Purchase : {}", purchase);
        if (purchase.getId() != null) {
            throw new BadRequestAlertException("A new purchase cannot already have an ID", ENTITY_NAME, "idexists");
        }
        purchase = purchaseRepository.save(purchase);
        return ResponseEntity.created(new URI("/api/purchases/" + purchase.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, purchase.getId().toString()))
            .body(purchase);
    }

    /**
     * {@code PUT  /purchases/:id} : Updates an existing purchase.
     *
     * @param id the id of the purchase to save.
     * @param purchase the purchase to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchase,
     * or with status {@code 400 (Bad Request)} if the purchase is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchase couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Purchase purchase
    ) throws URISyntaxException {
        LOG.debug("REST request to update Purchase : {}, {}", id, purchase);
        if (purchase.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchase.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        purchase = purchaseRepository.save(purchase);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchase.getId().toString()))
            .body(purchase);
    }

    /**
     * {@code PATCH  /purchases/:id} : Partial updates given fields of an existing purchase, field will ignore if it is null
     *
     * @param id the id of the purchase to save.
     * @param purchase the purchase to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchase,
     * or with status {@code 400 (Bad Request)} if the purchase is not valid,
     * or with status {@code 404 (Not Found)} if the purchase is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchase couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Purchase> partialUpdatePurchase(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Purchase purchase
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Purchase partially : {}, {}", id, purchase);
        if (purchase.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchase.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Purchase> result = purchaseRepository
            .findById(purchase.getId())
            .map(existingPurchase -> {
                if (purchase.getPurchaseDate() != null) {
                    existingPurchase.setPurchaseDate(purchase.getPurchaseDate());
                }

                return existingPurchase;
            })
            .map(purchaseRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchase.getId().toString())
        );
    }

    /**
     * {@code GET  /purchases} : get all the purchases.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchases in body.
     */
    @GetMapping("")
    public List<Purchase> getAllPurchases() {
        LOG.debug("REST request to get all Purchases");
        return purchaseRepository.findAll();
    }

    /**
     * {@code GET  /purchases/:id} : get the "id" purchase.
     *
     * @param id the id of the purchase to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchase, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchase(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Purchase : {}", id);
        Optional<Purchase> purchase = purchaseRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(purchase);
    }

    /**
     * {@code DELETE  /purchases/:id} : delete the "id" purchase.
     *
     * @param id the id of the purchase to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Purchase : {}", id);
        purchaseRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
