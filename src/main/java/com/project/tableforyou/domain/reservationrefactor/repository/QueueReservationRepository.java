package com.project.tableforyou.domain.reservationrefactor.repository;

import com.project.tableforyou.domain.reservationrefactor.entity.QueueReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface QueueReservationRepository extends JpaRepository<QueueReservation, Long> {
    int countByRestaurantIdAndDate(Long restaurantId, LocalDate date);

    Optional<QueueReservation> findByUserIdAndRestaurantIdAndDateAndIsCanceledFalseAndActiveFlagTrue(
            Long userId, Long restaurantId, LocalDate date
    );

    int countByRestaurantIdAndDateAndIsEnteredTrue(Long restaurantId, LocalDate date);

    int countByRestaurantIdAndDateAndIsCanceledTrue(Long restaurantId, LocalDate date);
}
