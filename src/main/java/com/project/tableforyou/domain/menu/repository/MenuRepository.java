package com.project.tableforyou.domain.menu.repository;

import com.project.tableforyou.domain.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Page<Menu> findByRestaurantIdAndNameContaining(Long restaurantId, String searchKeyword, Pageable pageable);

    Page<Menu> findByRestaurantId(Long restaurantId, Pageable pageable);

    Optional<Menu> findByRestaurantIdAndId(Long restaurantId, Long Id);

    @Modifying
    @Query("delete from Menu m where m.id in :ids")
    void deleteAllMenuByIdInQuery(@Param("ids") List<Long> ids);

    @Query("select m.menuImage from Menu m where m.id = :id")
    String findMenuImageById(@Param("id") Long id);

    @Modifying
    @Query("update Menu m set m.menuImage = :menuImage where m.id = :id")
    void updateMenuImageById(@Param("id") Long id, @Param("menuImage") String menuImage);
}