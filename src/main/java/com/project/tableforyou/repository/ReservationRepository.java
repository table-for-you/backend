package com.project.tableforyou.repository;

import com.project.tableforyou.domain.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Modifying      // 1차캐시에 데이터를 가져오는 것이 아니라 db를 직접 수정하는 것.
    @Query("update Reservation r set r.booking = r.booking - 1 where r.id = :id")
    void decreaseBooking(@Param("id") Long id);

    Page<Reservation> findByRestaurantId(Long restaurant_id, Pageable pageable);
}
