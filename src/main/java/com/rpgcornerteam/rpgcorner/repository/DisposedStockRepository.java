package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.DisposedStock;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DisposedStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DisposedStockRepository extends JpaRepository<DisposedStock, Long> {}
