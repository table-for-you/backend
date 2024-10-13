package com.project.tableforyou.domain.review.repository;

import com.project.tableforyou.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUser_Id(Long userId);
    List<Review> findByRestaurant_Id(Long restaurantId);

    boolean existsByUser_IdAndRestaurant_Id(Long userId, Long restaurantId);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.restaurant.id = :restaurantId")
    void deleteByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT r.user.username FROM Review r WHERE r.id = :reviewId")
    String findUsernameByReviewId(@Param("reviewId") Long reviewId);
}
