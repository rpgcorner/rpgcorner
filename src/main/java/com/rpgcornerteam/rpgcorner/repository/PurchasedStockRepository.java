package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.PurchasedStock;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PurchasedStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchasedStockRepository extends JpaRepository<PurchasedStock, Long> {}
