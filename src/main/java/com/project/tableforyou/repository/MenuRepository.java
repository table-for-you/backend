package com.project.tableforyou.repository;

import com.project.tableforyou.domain.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Page<Menu> findByRestaurantIdAndNameContaining(Long restaurant_id, String searchKeyword, Pageable pageable);

    Page<Menu> findByRestaurantId(Long restaurant_id, Pageable pageable);
}