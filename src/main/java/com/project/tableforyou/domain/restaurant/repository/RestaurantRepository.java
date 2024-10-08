package com.project.tableforyou.domain.restaurant.repository;

import com.project.tableforyou.domain.restaurant.entity.FoodType;
import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Page<Restaurant> findByStatusAndNameContainingOrDescriptionContaining(RestaurantStatus status, String searchKeyword1, String searchKeyword2, Pageable pageable);
    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);
    Page<Restaurant> findByRegionAndStatus(Region region, RestaurantStatus status, Pageable pageable);
    Page<Restaurant> findByLocationContainingAndStatus(String searchKeyword, RestaurantStatus status, Pageable pageable);
    Page<Restaurant> findByStatusAndUser_Nickname(RestaurantStatus status, String ownerName, Pageable pageable);
    Page<Restaurant> findByStatusAndNameContaining(RestaurantStatus status, String searchKeyword, Pageable pageable);
    Page<Restaurant> findByFoodTypeAndStatus(FoodType foodType, RestaurantStatus status, Pageable pageable);
    List<Restaurant> findByUser_Username(String username);
    List<Restaurant> findByUser_UsernameAndStatus(String username, RestaurantStatus status);
    List<Restaurant> findByUser_Id(Long userId);

    boolean existsByIdAndUser_Username(Long restaurantId, String ownerUsername);

    @Modifying
    @Query("update Restaurant r set r.usedSeats = r.usedSeats + :value where r.id = :id")
    void updateUsedSeats(@Param("id") Long id, @Param("value") int value); // JPQL의 id와 매핑하기 위해.

    @Query("SELECT r.user.username FROM Restaurant r WHERE r.id = :restaurantId")
    String findUsernameByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT r.name FROM Restaurant r WHERE r.id = :restaurantId")
    String findRestaurantNameByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT r.usedSeats FROM Restaurant r WHERE r.id = :restaurantId")
    int getRestaurantUsedSeatsByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Modifying
    @Query("DELETE FROM Restaurant r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query("select r.mainImage from Restaurant r where r.id = :id")
    String findMainImageById(@Param("id") Long id);

    @Modifying
    @Query("update Restaurant r set r.mainImage = :mainImage where r.id = :id")
    void updateMainImageById(@Param("id") Long id, @Param("mainImage") String mainImage);
}
