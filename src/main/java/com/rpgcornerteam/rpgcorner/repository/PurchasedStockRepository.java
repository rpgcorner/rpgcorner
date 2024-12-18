package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.DisposedStock;
import com.rpgcornerteam.rpgcorner.domain.PurchasedStock;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PurchasedStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchasedStockRepository extends JpaRepository<PurchasedStock, Long> {
    List<PurchasedStock> findByPurchase_Id(Long purchaseId);
}
