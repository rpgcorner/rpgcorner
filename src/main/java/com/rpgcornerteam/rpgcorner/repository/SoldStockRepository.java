package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.SoldStock;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SoldStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SoldStockRepository extends JpaRepository<SoldStock, Long> {}
