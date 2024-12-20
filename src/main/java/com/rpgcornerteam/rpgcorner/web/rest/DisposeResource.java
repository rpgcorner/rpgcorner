package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.Dispose;
import com.rpgcornerteam.rpgcorner.repository.DisposeRepository;
import com.rpgcornerteam.rpgcorner.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.Dispose}.
 */
@RestController
@RequestMapping("/api/disposes")
@Transactional
public class DisposeResource {

    private static final Logger LOG = LoggerFactory.getLogger(DisposeResource.class);

    private static final String ENTITY_NAME = "dispose";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DisposeRepository disposeRepository;

    public DisposeResource(DisposeRepository disposeRepository) {
        this.disposeRepository = disposeRepository;
    }

    /**
     * {@code POST  /disposes} : Create a new dispose.
     *
     * @param dispose the dispose to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dispose, or with status {@code 400 (Bad Request)} if the dispose has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Dispose> createDispose(@Valid @RequestBody Dispose dispose) throws URISyntaxException {
        LOG.debug("REST request to save Dispose : {}", dispose);
        if (dispose.getId() != null) {
            throw new BadRequestAlertException("A new dispose cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dispose = disposeRepository.save(dispose);
        return ResponseEntity.created(new URI("/api/disposes/" + dispose.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dispose.getId().toString()))
            .body(dispose);
    }

    /**
     * {@code PUT  /disposes/:id} : Updates an existing dispose.
     *
     * @param id the id of the dispose to save.
     * @param dispose the dispose to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dispose,
     * or with status {@code 400 (Bad Request)} if the dispose is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dispose couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Dispose> updateDispose(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Dispose dispose
    ) throws URISyntaxException {
        LOG.debug("REST request to update Dispose : {}, {}", id, dispose);
        if (dispose.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dispose.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disposeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dispose = disposeRepository.save(dispose);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dispose.getId().toString()))
            .body(dispose);
    }

    /**
     * {@code PATCH  /disposes/:id} : Partial updates given fields of an existing dispose, field will ignore if it is null
     *
     * @param id the id of the dispose to save.
     * @param dispose the dispose to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dispose,
     * or with status {@code 400 (Bad Request)} if the dispose is not valid,
     * or with status {@code 404 (Not Found)} if the dispose is not found,
     * or with status {@code 500 (Internal Server Error)} if the dispose couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Dispose> partialUpdateDispose(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Dispose dispose
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Dispose partially : {}, {}", id, dispose);
        if (dispose.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dispose.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!disposeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Dispose> result = disposeRepository
            .findById(dispose.getId())
            .map(existingDispose -> {
                if (dispose.getDisposeDate() != null) {
                    existingDispose.setDisposeDate(dispose.getDisposeDate());
                }
                if (dispose.getNote() != null) {
                    existingDispose.setNote(dispose.getNote());
                }

                return existingDispose;
            })
            .map(disposeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dispose.getId().toString())
        );
    }

    /**
     * {@code GET  /disposes} : get all the disposes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of disposes in body.
     */
    @GetMapping("")
    public List<Dispose> getAllDisposes() {
        LOG.debug("REST request to get all Disposes");
        return disposeRepository.findAll();
    }

    /**
     * {@code GET  /disposes/:id} : get the "id" dispose.
     *
     * @param id the id of the dispose to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dispose, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Dispose> getDispose(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Dispose : {}", id);
        Optional<Dispose> dispose = disposeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(dispose);
    }

    /**
     * {@code DELETE  /disposes/:id} : delete the "id" dispose.
     *
     * @param id the id of the dispose to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispose(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Dispose : {}", id);
        disposeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/search")
    public List<Dispose> searchPurchases(
        @RequestParam(required = false) String searchParam,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        LOG.debug("REST request to search Purchases by date range: startDate={}, endDate={}", startDate, endDate);
        if (searchParam != null && !searchParam.isEmpty()) {}
        if (startDate != null || endDate != null) {
            return disposeRepository.findByDisposeDateBetween(startDate, endDate);
        }
        return disposeRepository.findAll();
    }
}
