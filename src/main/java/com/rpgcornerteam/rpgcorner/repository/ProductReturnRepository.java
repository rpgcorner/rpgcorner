package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.ProductReturn;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductReturn entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductReturnRepository extends JpaRepository<ProductReturn, Long> {
    @Query("select productReturn from ProductReturn productReturn where productReturn.returnedByUser.login = ?#{authentication.name}")
    List<ProductReturn> findByReturnedByUserIsCurrentUser();
}
