package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.PurchasedStock;
import com.rpgcornerteam.rpgcorner.repository.PurchasedStockRepository;
import com.rpgcornerteam.rpgcorner.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.PurchasedStock}.
 */
@RestController
@RequestMapping("/api/purchased-stocks")
@Transactional
public class PurchasedStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(PurchasedStockResource.class);

    private static final String ENTITY_NAME = "purchasedStock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchasedStockRepository purchasedStockRepository;

    public PurchasedStockResource(PurchasedStockRepository purchasedStockRepository) {
        this.purchasedStockRepository = purchasedStockRepository;
    }

    /**
     * {@code POST  /purchased-stocks} : Create a new purchasedStock.
     *
     * @param purchasedStock the purchasedStock to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchasedStock, or with status {@code 400 (Bad Request)} if the purchasedStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PurchasedStock> createPurchasedStock(@RequestBody PurchasedStock purchasedStock) throws URISyntaxException {
        LOG.debug("REST request to save PurchasedStock : {}", purchasedStock);
        if (purchasedStock.getId() != null) {
            throw new BadRequestAlertException("A new purchasedStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        purchasedStock = purchasedStockRepository.save(purchasedStock);
        return ResponseEntity.created(new URI("/api/purchased-stocks/" + purchasedStock.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, purchasedStock.getId().toString()))
            .body(purchasedStock);
    }

    /**
     * {@code PUT  /purchased-stocks/:id} : Updates an existing purchasedStock.
     *
     * @param id the id of the purchasedStock to save.
     * @param purchasedStock the purchasedStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchasedStock,
     * or with status {@code 400 (Bad Request)} if the purchasedStock is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchasedStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchasedStock> updatePurchasedStock(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchasedStock purchasedStock
    ) throws URISyntaxException {
        LOG.debug("REST request to update PurchasedStock : {}, {}", id, purchasedStock);
        if (purchasedStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchasedStock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchasedStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        purchasedStock = purchasedStockRepository.save(purchasedStock);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchasedStock.getId().toString()))
            .body(purchasedStock);
    }

    /**
     * {@code PATCH  /purchased-stocks/:id} : Partial updates given fields of an existing purchasedStock, field will ignore if it is null
     *
     * @param id the id of the purchasedStock to save.
     * @param purchasedStock the purchasedStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchasedStock,
     * or with status {@code 400 (Bad Request)} if the purchasedStock is not valid,
     * or with status {@code 404 (Not Found)} if the purchasedStock is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchasedStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchasedStock> partialUpdatePurchasedStock(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchasedStock purchasedStock
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PurchasedStock partially : {}, {}", id, purchasedStock);
        if (purchasedStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchasedStock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchasedStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchasedStock> result = purchasedStockRepository
            .findById(purchasedStock.getId())
            .map(existingPurchasedStock -> {
                if (purchasedStock.getSupplie() != null) {
                    existingPurchasedStock.setSupplie(purchasedStock.getSupplie());
                }
                if (purchasedStock.getUnitPrice() != null) {
                    existingPurchasedStock.setUnitPrice(purchasedStock.getUnitPrice());
                }

                return existingPurchasedStock;
            })
            .map(purchasedStockRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, purchasedStock.getId().toString())
        );
    }

    /**
     * {@code GET  /purchased-stocks} : get all the purchasedStocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchasedStocks in body.
     */
    @GetMapping("")
    public List<PurchasedStock> getAllPurchasedStocks() {
        LOG.debug("REST request to get all PurchasedStocks");
        return purchasedStockRepository.findAll();
    }

    /**
     * {@code GET  /purchased-stocks/:id} : get the "id" purchasedStock.
     *
     * @param id the id of the purchasedStock to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchasedStock, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchasedStock> getPurchasedStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PurchasedStock : {}", id);
        Optional<PurchasedStock> purchasedStock = purchasedStockRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(purchasedStock);
    }

    /**
     * {@code DELETE  /purchased-stocks/:id} : delete the "id" purchasedStock.
     *
     * @param id the id of the purchasedStock to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchasedStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PurchasedStock : {}", id);
        purchasedStockRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
