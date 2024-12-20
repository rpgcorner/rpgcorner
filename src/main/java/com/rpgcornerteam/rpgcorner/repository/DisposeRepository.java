package com.rpgcornerteam.rpgcorner.repository;

import com.rpgcornerteam.rpgcorner.domain.Dispose;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Dispose entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DisposeRepository extends JpaRepository<Dispose, Long> {
    @Query("select dispose from Dispose dispose where dispose.disposedByUser.login = ?#{authentication.name}")
    List<Dispose> findByDisposedByUserIsCurrentUser();

    List<Dispose> findByDisposeDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
