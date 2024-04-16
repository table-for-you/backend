package com.project.tableforyou.domain.menu.repository;

import com.project.tableforyou.domain.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Page<Menu> findByRestaurantIdAndNameContaining(Long restaurantId, String searchKeyword, Pageable pageable);

    Page<Menu> findByRestaurantId(Long restaurantId, Pageable pageable);

    Optional<Menu> findByRestaurantIdAndId(Long restaurantId, Long Id);
}