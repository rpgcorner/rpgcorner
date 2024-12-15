package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.SoldStock;
import com.rpgcornerteam.rpgcorner.repository.SoldStockRepository;
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
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.SoldStock}.
 */
@RestController
@RequestMapping("/api/sold-stocks")
@Transactional
public class SoldStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(SoldStockResource.class);

    private static final String ENTITY_NAME = "soldStock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SoldStockRepository soldStockRepository;

    public SoldStockResource(SoldStockRepository soldStockRepository) {
        this.soldStockRepository = soldStockRepository;
    }

    /**
     * {@code POST  /sold-stocks} : Create a new soldStock.
     *
     * @param soldStock the soldStock to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new soldStock, or with status {@code 400 (Bad Request)} if the soldStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SoldStock> createSoldStock(@RequestBody SoldStock soldStock) throws URISyntaxException {
        LOG.debug("REST request to save SoldStock : {}", soldStock);
        if (soldStock.getId() != null) {
            throw new BadRequestAlertException("A new soldStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        soldStock = soldStockRepository.save(soldStock);
        return ResponseEntity.created(new URI("/api/sold-stocks/" + soldStock.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, soldStock.getId().toString()))
            .body(soldStock);
    }

    /**
     * {@code PUT  /sold-stocks/:id} : Updates an existing soldStock.
     *
     * @param id the id of the soldStock to save.
     * @param soldStock the soldStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated soldStock,
     * or with status {@code 400 (Bad Request)} if the soldStock is not valid,
     * or with status {@code 500 (Internal Server Error)} if the soldStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SoldStock> updateSoldStock(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SoldStock soldStock
    ) throws URISyntaxException {
        LOG.debug("REST request to update SoldStock : {}, {}", id, soldStock);
        if (soldStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, soldStock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!soldStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        soldStock = soldStockRepository.save(soldStock);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, soldStock.getId().toString()))
            .body(soldStock);
    }

    /**
     * {@code PATCH  /sold-stocks/:id} : Partial updates given fields of an existing soldStock, field will ignore if it is null
     *
     * @param id the id of the soldStock to save.
     * @param soldStock the soldStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated soldStock,
     * or with status {@code 400 (Bad Request)} if the soldStock is not valid,
     * or with status {@code 404 (Not Found)} if the soldStock is not found,
     * or with status {@code 500 (Internal Server Error)} if the soldStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SoldStock> partialUpdateSoldStock(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SoldStock soldStock
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SoldStock partially : {}, {}", id, soldStock);
        if (soldStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, soldStock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!soldStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SoldStock> result = soldStockRepository
            .findById(soldStock.getId())
            .map(existingSoldStock -> {
                if (soldStock.getSupplie() != null) {
                    existingSoldStock.setSupplie(soldStock.getSupplie());
                }
                if (soldStock.getUnitPrice() != null) {
                    existingSoldStock.setUnitPrice(soldStock.getUnitPrice());
                }
                if (soldStock.getReturnedSupplie() != null) {
                    existingSoldStock.setReturnedSupplie(soldStock.getReturnedSupplie());
                }

                return existingSoldStock;
            })
            .map(soldStockRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, soldStock.getId().toString())
        );
    }

    /**
     * {@code GET  /sold-stocks} : get all the soldStocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of soldStocks in body.
     */
    @GetMapping("")
    public List<SoldStock> getAllSoldStocks() {
        LOG.debug("REST request to get all SoldStocks");
        return soldStockRepository.findAll();
    }

    /**
     * {@code GET  /sold-stocks/:id} : get the "id" soldStock.
     *
     * @param id the id of the soldStock to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the soldStock, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SoldStock> getSoldStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SoldStock : {}", id);
        Optional<SoldStock> soldStock = soldStockRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(soldStock);
    }

    @GetMapping("sale/{saleId}")
    public List<SoldStock> getSoldStockBySaleId(@PathVariable("saleId") Long saleId) {
        LOG.debug("REST request to get SoldStock by saleId : {}", saleId);
        return soldStockRepository.findBySale_Id(saleId);
    }

    /**
     * {@code DELETE  /sold-stocks/:id} : delete the "id" soldStock.
     *
     * @param id the id of the soldStock to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoldStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SoldStock : {}", id);
        soldStockRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
