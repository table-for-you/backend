package com.project.tableforyou.domain.menu.repository;

import com.project.tableforyou.domain.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Page<Menu> findByRestaurantNameAndNameContaining(String restaurant, String searchKeyword, Pageable pageable);

    Page<Menu> findByRestaurantName(String restaurant_id, Pageable pageable);

    Optional<Menu> findByRestaurantNameAndId(String restaurant, Long Id);
}