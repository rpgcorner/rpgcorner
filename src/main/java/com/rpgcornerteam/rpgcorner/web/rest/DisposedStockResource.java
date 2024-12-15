package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.DisposedStock;
import com.rpgcornerteam.rpgcorner.domain.SoldStock;
import com.rpgcornerteam.rpgcorner.repository.DisposedStockRepository;
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
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.DisposedStock}.
 */
@RestController
@RequestMapping("/api/disposed-stocks")
@Transactional
public class DisposedStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(DisposedStockResource.class);

    private static final String ENTITY_NAME = "disposedStock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DisposedStockRepository disposedStockRepository;

    public DisposedStockResource(DisposedStockRepository disposedStockRepository) {
        this.disposedStockRepository = disposedStockRepository;
    }

    /**
     * {@code POST  /disposed-stocks} : Create a new disposedStock.
     *
     * @param disposedStock the disposedStock to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new disposedStock, or with status {@code 400 (Bad Request)} if the disposedStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DisposedStock> createDisposedStock(@RequestBody DisposedStock disposedStock) throws URISyntaxException {
        LOG.debug("REST request to save DisposedStock : {}", disposedStock);
        if (disposedStock.getId() != null) {
            throw new BadRequestAlertException("A new disposedStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        disposedStock = disposedStockRepository.save(disposedStock);
        return ResponseEntity.created(new URI("/api/disposed-stocks/" + disposedStock.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, disposedStock.getId().toString()))
            .body(disposedStock);
    }

    /**
     * {@code PUT  /disposed-stocks/:id} : Updates an existing disposedStock.
     *
     * @param id the id of the disposedStock to save.
     * @param disposedStock the disposedStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated disposedStock,
     * or with status {@code 400 (Bad Request)} if the disposedStock is not valid,
     * or with status {@code 500 (Internal Server Error)} if the disposedStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DisposedStock> updateDisposedStock(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DisposedStock disposedStock
    ) throws URISyntaxException {
        LOG.debug("REST request to update DisposedStock : {}, {}", id, disposedStock);
        if (disposedStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, disposedStock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disposedStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        disposedStock = disposedStockRepository.save(disposedStock);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, disposedStock.getId().toString()))
            .body(disposedStock);
    }

    /**
     * {@code PATCH  /disposed-stocks/:id} : Partial updates given fields of an existing disposedStock, field will ignore if it is null
     *
     * @param id the id of the disposedStock to save.
     * @param disposedStock the disposedStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated disposedStock,
     * or with status {@code 400 (Bad Request)} if the disposedStock is not valid,
     * or with status {@code 404 (Not Found)} if the disposedStock is not found,
     * or with status {@code 500 (Internal Server Error)} if the disposedStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DisposedStock> partialUpdateDisposedStock(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DisposedStock disposedStock
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DisposedStock partially : {}, {}", id, disposedStock);
        if (disposedStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, disposedStock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disposedStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DisposedStock> result = disposedStockRepository
            .findById(disposedStock.getId())
            .map(existingDisposedStock -> {
                if (disposedStock.getSupplie() != null) {
                    existingDisposedStock.setSupplie(disposedStock.getSupplie());
                }
                if (disposedStock.getUnitPrice() != null) {
                    existingDisposedStock.setUnitPrice(disposedStock.getUnitPrice());
                }

                return existingDisposedStock;
            })
            .map(disposedStockRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, disposedStock.getId().toString())
        );
    }

    /**
     * {@code GET  /disposed-stocks} : get all the disposedStocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of disposedStocks in body.
     */
    @GetMapping("")
    public List<DisposedStock> getAllDisposedStocks() {
        LOG.debug("REST request to get all DisposedStocks");
        return disposedStockRepository.findAll();
    }

    /**
     * {@code GET  /disposed-stocks/:id} : get the "id" disposedStock.
     *
     * @param id the id of the disposedStock to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the disposedStock, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DisposedStock> getDisposedStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DisposedStock : {}", id);
        Optional<DisposedStock> disposedStock = disposedStockRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(disposedStock);
    }

    /**
     * {@code DELETE  /disposed-stocks/:id} : delete the "id" disposedStock.
     *
     * @param id the id of the disposedStock to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisposedStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DisposedStock : {}", id);
        disposedStockRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("dispose/{disposeId}")
    public List<DisposedStock> getSoldStockBySaleId(@PathVariable("disposeId") Long disposeId) {
        LOG.debug("REST request to get DisposedStock by disposeId : {}", disposeId);
        return disposedStockRepository.findByDispose_Id(disposeId);
    }
}
