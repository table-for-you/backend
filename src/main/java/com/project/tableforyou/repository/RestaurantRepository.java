package com.project.tableforyou.repository;

import com.project.tableforyou.domain.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Page<Restaurant> findByNameContainingOrDescriptionContaining(String searchKeyword1, String searchKeyword2, Pageable pageable);

    @Modifying
    @Query("update Restaurant r set r.usedSeats = r.usedSeats + :value where r.id = :id")
    void updateUsedSeats(@Param("id") Long id, @Param("value") int value); // JPQL의 id와 매핑하기 위해.

    @Modifying
    @Query("update Restaurant r set r.likeCount = r.likeCount + :value where r.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("value") int value);

}
