package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.Category;
import com.rpgcornerteam.rpgcorner.domain.Supplier;
import com.rpgcornerteam.rpgcorner.domain.Ware;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Ware entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WareRepository extends JpaRepository<Ware, Long> {
    List<Ware> findByNameContainingIgnoreCase(String name);
    List<Ware> findByActive(Boolean active);
    List<Ware> findByNameContainingIgnoreCaseAndActive(String name, Boolean active);
}
