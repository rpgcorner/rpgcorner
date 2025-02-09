package com.rpgcornerteam.rpgcorner.web.rest;

import com.rpgcornerteam.rpgcorner.domain.Category;
import com.rpgcornerteam.rpgcorner.domain.enumeration.CategoryTypeEnum;
import com.rpgcornerteam.rpgcorner.repository.CategoryRepository;
import com.rpgcornerteam.rpgcorner.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rpgcornerteam.rpgcorner.domain.Category}.
 */
@RestController
@RequestMapping("/api/categories")
@Transactional
public class CategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryResource.class);

    private static final String ENTITY_NAME = "category";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CategoryRepository categoryRepository;

    public CategoryResource(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * {@code POST  /categories} : Create a new category.
     *
     * @param category the category to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new category, or with status {@code 400 (Bad Request)} if the category has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) throws URISyntaxException {
        LOG.debug("REST request to save Category : {}", category);
        if (category.getId() != null) {
            throw new BadRequestAlertException("A new category cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (category.getCategoryType() == CategoryTypeEnum.MAIN_CATEGORY) {
            List<Long> subCategoriId = new ArrayList<>();
            for (Category subCategory : category.getSubCategories()) {
                if (subCategoriId.contains(subCategory.getId())) {
                    HttpHeaders headers = HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, null);

                    headers.add(
                        "X-app-warning",
                        "A megadott al kategória már létezik a kiválasztott fő kategóriában. Kérjük, válasszon másikat!"
                    );
                    return ResponseEntity.created(new URI("/api/category/")).headers(headers).body(null);
                } else {
                    subCategoriId.add(subCategory.getId());
                }
            }
        }
        category = categoryRepository.save(category);
        return ResponseEntity.created(new URI("/api/categories/" + category.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, category.getId().toString()))
            .body(category);
    }

    /**
     * {@code PUT  /categories/:id} : Updates an existing category.
     *
     * @param id       the id of the category to save.
     * @param category the category to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated category,
     * or with status {@code 400 (Bad Request)} if the category is not valid,
     * or with status {@code 500 (Internal Server Error)} if the category couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Category category
    ) throws URISyntaxException {
        LOG.debug("REST request to update Category : {}, {}", id, category);
        if (category.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, category.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        category = categoryRepository.save(category);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, category.getId().toString()))
            .body(category);
    }

    /**
     * {@code PATCH  /categories/:id} : Partial updates given fields of an existing category, field will ignore if it is null
     *
     * @param id       the id of the category to save.
     * @param category the category to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated category,
     * or with status {@code 400 (Bad Request)} if the category is not valid,
     * or with status {@code 404 (Not Found)} if the category is not found,
     * or with status {@code 500 (Internal Server Error)} if the category couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Category> partialUpdateCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Category category
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Category partially : {}, {}", id, category);
        if (category.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, category.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Category> result = categoryRepository
            .findById(category.getId())
            .map(existingCategory -> {
                if (category.getActive() != null) {
                    existingCategory.setActive(category.getActive());
                }
                if (category.getCategoryType() != null) {
                    existingCategory.setCategoryType(category.getCategoryType());
                }
                if (category.getDescription() != null) {
                    existingCategory.setDescription(category.getDescription());
                }

                return existingCategory;
            })
            .map(categoryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, category.getId().toString())
        );
    }

    /**
     * {@code GET  /categories} : get all the categories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categories in body.
     */
    @GetMapping("")
    public List<Category> getAllCategories() {
        LOG.debug("REST request to get all Categories");
        return categoryRepository.findAll();
    }

    /**
     * {@code GET  /categories/:id} : get the "id" category.
     *
     * @param id the id of the category to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the category, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Category : {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(category);
    }

    /**
     * {@code DELETE  /categories/:id} : delete the "id" category.
     *
     * @param id the id of the category to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Category : {}", id);
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/search")
    public List<Category> searchCategories(@RequestParam(required = false) String name, @RequestParam(required = false) Boolean active) {
        LOG.debug("REST request to search Categories by name and active status");
        if (name != null && !name.isEmpty() && active != null) {
            return categoryRepository.findByDescriptionContainingIgnoreCaseAndActive(name, active);
        } else if (name != null && !name.isEmpty()) {
            return categoryRepository.findByDescriptionContainingIgnoreCase(name);
        } else if (active != null) {
            return categoryRepository.findByActive(active);
        } else {
            return categoryRepository.findAll();
        }
    }
}
