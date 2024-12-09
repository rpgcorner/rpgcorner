package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.ReturnedStock;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnedStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReturnedStockRepository extends JpaRepository<ReturnedStock, Long> {}
