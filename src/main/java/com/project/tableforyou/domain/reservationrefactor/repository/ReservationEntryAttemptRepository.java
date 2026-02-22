package com.project.tableforyou.domain.reservationrefactor.repository;

import com.project.tableforyou.domain.reservationrefactor.entity.ReservationEntryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationEntryAttemptRepository extends JpaRepository<ReservationEntryAttempt, Long> {
    Optional<ReservationEntryAttempt> findByAttemptId(String attemptId);
}
