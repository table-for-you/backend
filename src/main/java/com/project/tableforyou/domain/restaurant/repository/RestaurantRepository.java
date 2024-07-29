package com.project.tableforyou.domain.restaurant.repository;

import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Page<Restaurant> findByStatusAndNameContainingOrDescriptionContaining(RestaurantStatus status, String searchKeyword1, String searchKeyword2, Pageable pageable);
    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);
    Page<Restaurant> findByRegionAndStatus(Region region, RestaurantStatus status, Pageable pageable);
    Page<Restaurant> findByLocationContainingAndStatus(String searchKeyword, RestaurantStatus status, Pageable pageable);
    Page<Restaurant> findByStatusAndUser_Nickname(RestaurantStatus status, String ownerName, Pageable pageable);
    Page<Restaurant> findByStatusAndNameContaining(RestaurantStatus status, String searchKeyword, Pageable pageable);
    List<Restaurant> findByUser_Username(String username);

    boolean existsByIdAndUser_Username(Long restaurantId, String ownerUsername);

    @Modifying
    @Query("update Restaurant r set r.usedSeats = r.usedSeats + :value where r.id = :id")
    void updateUsedSeats(@Param("id") Long id, @Param("value") int value); // JPQL의 id와 매핑하기 위해.

    @Query("SELECT r.user.username FROM Restaurant r WHERE r.id = :restaurantId")
    String findUsernameByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Modifying
    @Query("delete from Restaurant r where r.id in :ids")
    void deleteAllRestaurantByIdInQuery(@Param("ids") List<Long> ids);
}
