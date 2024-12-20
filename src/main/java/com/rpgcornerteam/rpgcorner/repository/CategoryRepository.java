package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDescriptionContainingIgnoreCase(String name);
    List<Category> findByActive(Boolean active);
    List<Category> findByDescriptionContainingIgnoreCaseAndActive(String name, Boolean active);
}
