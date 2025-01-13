package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.Dispose;
import com.rpgcornerteam.rpgcorner.domain.DisposedStock;
import com.rpgcornerteam.rpgcorner.domain.Sale;
import com.rpgcornerteam.rpgcorner.domain.SoldStock;
import com.rpgcornerteam.rpgcorner.repository.DisposedStockRepository;
import com.rpgcornerteam.rpgcorner.repository.InventoryRepository;
import com.rpgcornerteam.rpgcorner.repository.SaleRepository;
import com.rpgcornerteam.rpgcorner.repository.SoldStockRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.Sale}.
 */
@RestController
@RequestMapping("/api/sales")
@Transactional
public class SaleResource {

    private static final Logger LOG = LoggerFactory.getLogger(SaleResource.class);

    private static final String ENTITY_NAME = "sale";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SaleRepository saleRepository;
    private final SoldStockRepository soldStockRepository;
    private final InventoryRepository inventoryRepository;

    public SaleResource(SaleRepository saleRepository, SoldStockRepository soldStockRepository, InventoryRepository inventoryRepository) {
        this.saleRepository = saleRepository;
        this.inventoryRepository = inventoryRepository;
        this.soldStockRepository = soldStockRepository;
    }

    /**
     * {@code POST  /sales} : Create a new sale.
     *
     * @param sale the sale to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sale, or with status {@code 400 (Bad Request)} if the sale has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Sale> createSale(@Valid @RequestBody Sale sale) throws URISyntaxException {
        LOG.debug("REST request to save Sale : {}", sale);
        if (sale.getId() != null) {
            throw new BadRequestAlertException("A new sale cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sale = saleRepository.save(sale);
        return ResponseEntity.created(new URI("/api/sales/" + sale.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sale.getId().toString()))
            .body(sale);
    }

    // Segédmetódus a hibaüzenet kezelésére
    private ResponseEntity<Sale> createWarningResponse(String warningMessage) {
        HttpHeaders headers = HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, "");
        headers.add("X-app-warning", warningMessage);
        return ResponseEntity.created(URI.create("/api/sale/")).headers(headers).body(null);
    }

    /**
     * {@code PUT  /sales/:id} : Updates an existing sale.
     *
     * @param id the id of the sale to save.
     * @param sale the sale to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sale,
     * or with status {@code 400 (Bad Request)} if the sale is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sale couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Sale> updateSale(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Sale sale)
        throws URISyntaxException {
        if (sale.getTransactionClosed()) {
            List<SoldStock> disposedStockList = soldStockRepository.findBySale_Id(sale.getId());
            for (SoldStock disposedStock : disposedStockList) {
                if (disposedStock.getSoldWare().getInventory().getSupplie() < disposedStock.getSupplie()) {
                    return createWarningResponse(
                        "Csak olyan árucikk vásárolható, amelyből van raktárkészlet, és a vásárlás nem haladhatja meg a rendelkezésre álló mennyiséget."
                    );
                } else {
                    disposedStock
                        .getSoldWare()
                        .getInventory()
                        .setSupplie(disposedStock.getSoldWare().getInventory().getSupplie() - disposedStock.getSupplie());
                    inventoryRepository.save(disposedStock.getSoldWare().getInventory());
                }

                if (disposedStock.getSupplie() <= 0) {
                    return createWarningResponse("Minimum egy darabot meg kell adni!");
                }
            }

            if (disposedStockList.isEmpty()) {
                return createWarningResponse("Minimum egy árucikket meg kell adni!");
            }
        }

        LOG.debug("REST request to update Sale : {}, {}", id, sale);
        if (sale.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sale.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sale = saleRepository.save(sale);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sale.getId().toString()))
            .body(sale);
    }

    /**
     * {@code PATCH  /sales/:id} : Partial updates given fields of an existing sale, field will ignore if it is null
     *
     * @param id the id of the sale to save.
     * @param sale the sale to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sale,
     * or with status {@code 400 (Bad Request)} if the sale is not valid,
     * or with status {@code 404 (Not Found)} if the sale is not found,
     * or with status {@code 500 (Internal Server Error)} if the sale couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Sale> partialUpdateSale(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Sale sale
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Sale partially : {}, {}", id, sale);
        if (sale.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sale.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sale> result = saleRepository
            .findById(sale.getId())
            .map(existingSale -> {
                if (sale.getSoldDate() != null) {
                    existingSale.setSoldDate(sale.getSoldDate());
                }

                return existingSale;
            })
            .map(saleRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sale.getId().toString())
        );
    }

    /**
     * {@code GET  /sales} : get all the sales.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sales in body.
     */
    @GetMapping("")
    public List<Sale> getAllSales() {
        LOG.debug("REST request to get all Sales");
        return saleRepository.findAll();
    }

    /**
     * {@code GET  /sales/:id} : get the "id" sale.
     *
     * @param id the id of the sale to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sale, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSale(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Sale : {}", id);
        Optional<Sale> sale = saleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sale);
    }

    /**
     * {@code DELETE  /sales/:id} : delete the "id" sale.
     *
     * @param id the id of the sale to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Sale : {}", id);
        saleRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/search")
    public List<Sale> searchPurchases(
        @RequestParam(required = false) String searchParam,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        LOG.debug("REST request to search Purchases by date range: startDate={}, endDate={}", startDate, endDate);
        if (startDate != null || endDate != null) {
            return saleRepository.findBySoldDateBetween(startDate, endDate);
        }
        return saleRepository.findAll();
    }
}
