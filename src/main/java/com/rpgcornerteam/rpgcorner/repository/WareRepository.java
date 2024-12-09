package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.Ware;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Ware entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WareRepository extends JpaRepository<Ware, Long> {}
