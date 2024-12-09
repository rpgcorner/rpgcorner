package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Sale entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("select sale from Sale sale where sale.soldByUser.login = ?#{authentication.name}")
    List<Sale> findBySoldByUserIsCurrentUser();
}
