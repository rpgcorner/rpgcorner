package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.ProductReturn;
import com.rpgcornerteam.rpgcorner.domain.ReturnedStock;
import com.rpgcornerteam.rpgcorner.domain.Sale;
import com.rpgcornerteam.rpgcorner.domain.SoldStock;
import com.rpgcornerteam.rpgcorner.repository.InventoryRepository;
import com.rpgcornerteam.rpgcorner.repository.ProductReturnRepository;
import com.rpgcornerteam.rpgcorner.repository.ReturnedStockRepository;
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
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.ProductReturn}.
 */
@RestController
@RequestMapping("/api/product-returns")
@Transactional
public class ProductReturnResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductReturnResource.class);

    private static final String ENTITY_NAME = "productReturn";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductReturnRepository productReturnRepository;
    private final ReturnedStockRepository returnedStockRepository;
    private final InventoryRepository inventoryRepository;

    public ProductReturnResource(
        ProductReturnRepository productReturnRepository,
        InventoryRepository inventoryRepository,
        ReturnedStockRepository returnedStockRepository
    ) {
        this.returnedStockRepository = returnedStockRepository;
        this.productReturnRepository = productReturnRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * {@code POST  /product-returns} : Create a new productReturn.
     *
     * @param productReturn the productReturn to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productReturn, or with status {@code 400 (Bad Request)} if the productReturn has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductReturn> createProductReturn(@Valid @RequestBody ProductReturn productReturn) throws URISyntaxException {
        LOG.debug("REST request to save ProductReturn : {}", productReturn);
        if (productReturn.getId() != null) {
            throw new BadRequestAlertException("A new productReturn cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productReturn = productReturnRepository.save(productReturn);
        return ResponseEntity.created(new URI("/api/product-returns/" + productReturn.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productReturn.getId().toString()))
            .body(productReturn);
    }

    // Segédmetódus a hibaüzenet kezelésére
    private ResponseEntity<ProductReturn> createWarningResponse(String warningMessage) {
        HttpHeaders headers = HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, "");
        headers.add("X-app-warning", warningMessage);
        return ResponseEntity.created(URI.create("/api/productReturn/")).headers(headers).body(null);
    }

    /**
     * {@code PUT  /product-returns/:id} : Updates an existing productReturn.
     *
     * @param id the id of the productReturn to save.
     * @param productReturn the productReturn to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productReturn,
     * or with status {@code 400 (Bad Request)} if the productReturn is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productReturn couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductReturn> updateProductReturn(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductReturn productReturn
    ) throws URISyntaxException {
        if (productReturn.getTransactionClosed()) {
            List<ReturnedStock> disposedStockList = returnedStockRepository.findByProductReturn_Id(productReturn.getId());
            for (ReturnedStock disposedStock : disposedStockList) {
                if (disposedStock.getReturnedWare().getInventory().getSupplie() < disposedStock.getSupplie()) {
                    return createWarningResponse("Csak annyi áru hozható vissza, amennyi korábban megvásárlásra került.");
                } else {
                    disposedStock
                        .getReturnedWare()
                        .getInventory()
                        .setSupplie(disposedStock.getReturnedWare().getInventory().getSupplie() + disposedStock.getSupplie());
                    inventoryRepository.save(disposedStock.getReturnedWare().getInventory());
                }

                if (disposedStock.getSupplie() <= 0) {
                    return createWarningResponse("Minimum egy darabot meg kell adni!");
                }
            }

            if (disposedStockList.isEmpty()) {
                return createWarningResponse("Minimum egy árucikket meg kell adni!");
            }
        }
        LOG.debug("REST request to update ProductReturn : {}, {}", id, productReturn);
        if (productReturn.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productReturn.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productReturnRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productReturn = productReturnRepository.save(productReturn);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productReturn.getId().toString()))
            .body(productReturn);
    }

    /**
     * {@code PATCH  /product-returns/:id} : Partial updates given fields of an existing productReturn, field will ignore if it is null
     *
     * @param id the id of the productReturn to save.
     * @param productReturn the productReturn to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productReturn,
     * or with status {@code 400 (Bad Request)} if the productReturn is not valid,
     * or with status {@code 404 (Not Found)} if the productReturn is not found,
     * or with status {@code 500 (Internal Server Error)} if the productReturn couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductReturn> partialUpdateProductReturn(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductReturn productReturn
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductReturn partially : {}, {}", id, productReturn);
        if (productReturn.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productReturn.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productReturnRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductReturn> result = productReturnRepository
            .findById(productReturn.getId())
            .map(existingProductReturn -> {
                if (productReturn.getReturnDate() != null) {
                    existingProductReturn.setReturnDate(productReturn.getReturnDate());
                }
                if (productReturn.getNote() != null) {
                    existingProductReturn.setNote(productReturn.getNote());
                }

                return existingProductReturn;
            })
            .map(productReturnRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productReturn.getId().toString())
        );
    }

    /**
     * {@code GET  /product-returns} : get all the productReturns.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productReturns in body.
     */
    @GetMapping("")
    public List<ProductReturn> getAllProductReturns() {
        LOG.debug("REST request to get all ProductReturns");
        return productReturnRepository.findAll();
    }

    /**
     * {@code GET  /product-returns/:id} : get the "id" productReturn.
     *
     * @param id the id of the productReturn to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productReturn, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductReturn> getProductReturn(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductReturn : {}", id);
        Optional<ProductReturn> productReturn = productReturnRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(productReturn);
    }

    /**
     * {@code DELETE  /product-returns/:id} : delete the "id" productReturn.
     *
     * @param id the id of the productReturn to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductReturn(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductReturn : {}", id);
        productReturnRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/search")
    public List<ProductReturn> searchPurchases(
        @RequestParam(required = false) String searchParam,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        LOG.debug("REST request to search Purchases by date range: startDate={}, endDate={}", startDate, endDate);
        if (startDate != null || endDate != null) {
            return productReturnRepository.findByReturnDateBetween(startDate, endDate);
        }
        return productReturnRepository.findAll();
    }
}
