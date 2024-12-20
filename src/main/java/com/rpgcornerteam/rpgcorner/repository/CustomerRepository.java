package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.Customer;
import com.rpgcornerteam.rpgcorner.domain.Purchase;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query(
        "SELECT c FROM Customer c WHERE " +
        "LOWER(c.contact.contactName) LIKE LOWER(CONCAT('%', :searchParam, '%')) OR " +
        "LOWER(c.contact.address) LIKE LOWER(CONCAT('%', :searchParam, '%'))"
    )
    List<Customer> findByContactNameOrAddressContainingIgnoreCase(@Param("searchParam") String searchParam);
}
