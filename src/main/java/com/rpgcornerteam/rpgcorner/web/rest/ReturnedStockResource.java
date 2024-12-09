package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.ReturnedStock;
import com.rpgcornerteam.rpgcorner.repository.ReturnedStockRepository;
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
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.ReturnedStock}.
 */
@RestController
@RequestMapping("/api/returned-stocks")
@Transactional
public class ReturnedStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnedStockResource.class);

    private static final String ENTITY_NAME = "returnedStock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnedStockRepository returnedStockRepository;

    public ReturnedStockResource(ReturnedStockRepository returnedStockRepository) {
        this.returnedStockRepository = returnedStockRepository;
    }

    /**
     * {@code POST  /returned-stocks} : Create a new returnedStock.
     *
     * @param returnedStock the returnedStock to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnedStock, or with status {@code 400 (Bad Request)} if the returnedStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReturnedStock> createReturnedStock(@RequestBody ReturnedStock returnedStock) throws URISyntaxException {
        LOG.debug("REST request to save ReturnedStock : {}", returnedStock);
        if (returnedStock.getId() != null) {
            throw new BadRequestAlertException("A new returnedStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        returnedStock = returnedStockRepository.save(returnedStock);
        return ResponseEntity.created(new URI("/api/returned-stocks/" + returnedStock.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, returnedStock.getId().toString()))
            .body(returnedStock);
    }

    /**
     * {@code PUT  /returned-stocks/:id} : Updates an existing returnedStock.
     *
     * @param id the id of the returnedStock to save.
     * @param returnedStock the returnedStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnedStock,
     * or with status {@code 400 (Bad Request)} if the returnedStock is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnedStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReturnedStock> updateReturnedStock(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReturnedStock returnedStock
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReturnedStock : {}, {}", id, returnedStock);
        if (returnedStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnedStock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnedStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        returnedStock = returnedStockRepository.save(returnedStock);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnedStock.getId().toString()))
            .body(returnedStock);
    }

    /**
     * {@code PATCH  /returned-stocks/:id} : Partial updates given fields of an existing returnedStock, field will ignore if it is null
     *
     * @param id the id of the returnedStock to save.
     * @param returnedStock the returnedStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnedStock,
     * or with status {@code 400 (Bad Request)} if the returnedStock is not valid,
     * or with status {@code 404 (Not Found)} if the returnedStock is not found,
     * or with status {@code 500 (Internal Server Error)} if the returnedStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReturnedStock> partialUpdateReturnedStock(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReturnedStock returnedStock
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReturnedStock partially : {}, {}", id, returnedStock);
        if (returnedStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnedStock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnedStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReturnedStock> result = returnedStockRepository
            .findById(returnedStock.getId())
            .map(existingReturnedStock -> {
                if (returnedStock.getSupplie() != null) {
                    existingReturnedStock.setSupplie(returnedStock.getSupplie());
                }
                if (returnedStock.getUnitPrice() != null) {
                    existingReturnedStock.setUnitPrice(returnedStock.getUnitPrice());
                }

                return existingReturnedStock;
            })
            .map(returnedStockRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnedStock.getId().toString())
        );
    }

    /**
     * {@code GET  /returned-stocks} : get all the returnedStocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of returnedStocks in body.
     */
    @GetMapping("")
    public List<ReturnedStock> getAllReturnedStocks() {
        LOG.debug("REST request to get all ReturnedStocks");
        return returnedStockRepository.findAll();
    }

    /**
     * {@code GET  /returned-stocks/:id} : get the "id" returnedStock.
     *
     * @param id the id of the returnedStock to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnedStock, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReturnedStock> getReturnedStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReturnedStock : {}", id);
        Optional<ReturnedStock> returnedStock = returnedStockRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(returnedStock);
    }

    /**
     * {@code DELETE  /returned-stocks/:id} : delete the "id" returnedStock.
     *
     * @param id the id of the returnedStock to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnedStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReturnedStock : {}", id);
        returnedStockRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
