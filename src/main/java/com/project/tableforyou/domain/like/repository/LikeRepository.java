package com.project.tableforyou.domain.like.repository;

import com.project.tableforyou.domain.like.entity.Like;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndRestaurant(User user, Restaurant restaurant);

    List<Like> findByUser_Username(String username);

    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.restaurant.id IN (SELECT r.id FROM Restaurant r WHERE r.user.id = :userId)")
    void deleteRestaurantLikeByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.restaurant.id = :restaurantId")
    void deleteByRestaurantId(@Param("restaurantId") Long restaurantId);
}
