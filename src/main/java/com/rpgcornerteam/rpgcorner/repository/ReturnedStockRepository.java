package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.PurchasedStock;
import com.rpgcornerteam.rpgcorner.domain.ReturnedStock;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnedStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReturnedStockRepository extends JpaRepository<ReturnedStock, Long> {
    List<ReturnedStock> findByProductReturn_Id(Long returnedProductId);
}
