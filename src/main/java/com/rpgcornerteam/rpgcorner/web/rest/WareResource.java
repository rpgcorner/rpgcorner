package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.Ware;
import com.rpgcornerteam.rpgcorner.repository.WareRepository;
import com.rpgcornerteam.rpgcorner.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.Ware}.
 */
@RestController
@RequestMapping("/api/wares")
@Transactional
public class WareResource {

    private static final Logger LOG = LoggerFactory.getLogger(WareResource.class);

    private static final String ENTITY_NAME = "ware";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WareRepository wareRepository;

    public WareResource(WareRepository wareRepository) {
        this.wareRepository = wareRepository;
    }

    /**
     * {@code POST  /wares} : Create a new ware.
     *
     * @param ware the ware to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ware, or with status {@code 400 (Bad Request)} if the ware has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Ware> createWare(@Valid @RequestBody Ware ware) throws URISyntaxException {
        LOG.debug("REST request to save Ware : {}", ware);
        if (ware.getId() != null) {
            throw new BadRequestAlertException("A new ware cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ware = wareRepository.save(ware);
        return ResponseEntity.created(new URI("/api/wares/" + ware.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ware.getId().toString()))
            .body(ware);
    }

    /**
     * {@code PUT  /wares/:id} : Updates an existing ware.
     *
     * @param id the id of the ware to save.
     * @param ware the ware to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ware,
     * or with status {@code 400 (Bad Request)} if the ware is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ware couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ware> updateWare(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Ware ware)
        throws URISyntaxException {
        LOG.debug("REST request to update Ware : {}, {}", id, ware);
        if (ware.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ware.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wareRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ware = wareRepository.save(ware);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ware.getId().toString()))
            .body(ware);
    }

    /**
     * {@code PATCH  /wares/:id} : Partial updates given fields of an existing ware, field will ignore if it is null
     *
     * @param id the id of the ware to save.
     * @param ware the ware to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ware,
     * or with status {@code 400 (Bad Request)} if the ware is not valid,
     * or with status {@code 404 (Not Found)} if the ware is not found,
     * or with status {@code 500 (Internal Server Error)} if the ware couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Ware> partialUpdateWare(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Ware ware
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Ware partially : {}, {}", id, ware);
        if (ware.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ware.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wareRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Ware> result = wareRepository
            .findById(ware.getId())
            .map(existingWare -> {
                if (ware.getActive() != null) {
                    existingWare.setActive(ware.getActive());
                }
                if (ware.getCreated() != null) {
                    existingWare.setCreated(ware.getCreated());
                }
                if (ware.getName() != null) {
                    existingWare.setName(ware.getName());
                }
                if (ware.getDescription() != null) {
                    existingWare.setDescription(ware.getDescription());
                }
                if (ware.getProductCode() != null) {
                    existingWare.setProductCode(ware.getProductCode());
                }

                return existingWare;
            })
            .map(wareRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ware.getId().toString())
        );
    }

    /**
     * {@code GET  /wares} : get all the wares.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wares in body.
     */
    @GetMapping("")
    public List<Ware> getAllWares(@RequestParam(name = "filter", required = false) String filter) {
        if ("inventory-is-null".equals(filter)) {
            LOG.debug("REST request to get all Wares where inventory is null");
            return StreamSupport.stream(wareRepository.findAll().spliterator(), false).filter(ware -> ware.getInventory() == null).toList();
        }

        if ("disposedstock-is-null".equals(filter)) {
            LOG.debug("REST request to get all Wares where disposedStock is null");
            return StreamSupport.stream(wareRepository.findAll().spliterator(), false)
                .filter(ware -> ware.getDisposedStock() == null)
                .toList();
        }

        if ("purchasedstock-is-null".equals(filter)) {
            LOG.debug("REST request to get all Wares where purchasedStock is null");
            return StreamSupport.stream(wareRepository.findAll().spliterator(), false)
                .filter(ware -> ware.getPurchasedStock() == null)
                .toList();
        }

        if ("returnedstock-is-null".equals(filter)) {
            LOG.debug("REST request to get all Wares where returnedStock is null");
            return StreamSupport.stream(wareRepository.findAll().spliterator(), false)
                .filter(ware -> ware.getReturnedStock() == null)
                .toList();
        }

        if ("soldstock-is-null".equals(filter)) {
            LOG.debug("REST request to get all Wares where soldStock is null");
            return StreamSupport.stream(wareRepository.findAll().spliterator(), false).filter(ware -> ware.getSoldStock() == null).toList();
        }
        LOG.debug("REST request to get all Wares");
        return wareRepository.findAll();
    }

    /**
     * {@code GET  /wares/:id} : get the "id" ware.
     *
     * @param id the id of the ware to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ware, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ware> getWare(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Ware : {}", id);
        Optional<Ware> ware = wareRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ware);
    }

    /**
     * {@code DELETE  /wares/:id} : delete the "id" ware.
     *
     * @param id the id of the ware to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWare(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Ware : {}", id);
        wareRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
