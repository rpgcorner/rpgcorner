package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.Purchase;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Purchase entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("select purchase from Purchase purchase where purchase.purchasedByUser.login = ?#{authentication.name}")
    List<Purchase> findByPurchasedByUserIsCurrentUser();

    List<Purchase> findByPurchaseDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
