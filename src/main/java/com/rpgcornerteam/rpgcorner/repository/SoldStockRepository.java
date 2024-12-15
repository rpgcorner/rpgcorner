package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.SoldStock;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SoldStock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SoldStockRepository extends JpaRepository<SoldStock, Long> {
    /**
     * Finds all SoldStock entities associated with a specific Sale ID.
     *
     * @param saleId the ID of the sale
     * @return a list of SoldStock entities associated with the given Sale ID
     */
    List<SoldStock> findBySale_Id(Long saleId);
}
