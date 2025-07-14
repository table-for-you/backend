package com.project.tableforyou.domain.reservationrefactor.repository;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservationrefactor.entity.TimeSlotReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TimeSlotReservationRepository extends JpaRepository<TimeSlotReservation, Long> {
    int countByRestaurantIdAndDateAndTimeSlotAndIsCanceledFalse(Long restaurantId, LocalDate date, TimeSlot timeSlot);

    Optional<TimeSlotReservation> findByUserIdAndRestaurantIdAndDateAndTimeSlotAndIsCanceledFalse(
            Long userId, Long restaurantId, LocalDate date, TimeSlot timeSlot
    );
}
