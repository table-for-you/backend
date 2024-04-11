package com.project.tableforyou.domain.like.repository;

import com.project.tableforyou.domain.like.entity.Like;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndRestaurant(User user, Restaurant restaurant);

    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);
}
