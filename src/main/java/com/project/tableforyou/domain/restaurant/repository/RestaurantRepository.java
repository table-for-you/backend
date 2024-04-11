package com.project.tableforyou.domain.restaurant.repository;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Page<Restaurant> findByNameContainingOrDescriptionContaining(String searchKeyword1, String searchKeyword2, Pageable pageable);
    Page<Restaurant> findByUserNot(User user, Pageable pageable);
    Page<Restaurant> findByUser(User user, Pageable pageable);
    Optional<Restaurant> findByName(String name);

    @Modifying
    @Query("update Restaurant r set r.usedSeats = r.usedSeats + :value where r.name = :name")
    void updateUsedSeats(@Param("name") String name, @Param("value") int value); // JPQL의 id와 매핑하기 위해.

}
