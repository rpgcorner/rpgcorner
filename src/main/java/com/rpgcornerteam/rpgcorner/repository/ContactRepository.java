package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.Contact;
import com.rpgcornerteam.rpgcorner.domain.Customer;
import com.rpgcornerteam.rpgcorner.domain.ReturnedStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Contact entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findBySupplier_Id(Long supplierId);

    List<Contact> findByEmail(String email);
}
