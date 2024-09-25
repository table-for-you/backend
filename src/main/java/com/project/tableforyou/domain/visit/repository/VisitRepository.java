package com.project.tableforyou.domain.visit.repository;

import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByVisitor(User visitor);

    void deleteByVisitor_UsernameAndRestaurant_Id(String username, Long restaurantId);

    @Modifying
    @Query("DELETE FROM Visit v WHERE v.visitor.id = :visitorId")
    void deleteByVisitorId(@Param("visitorId") Long visitorId);

    @Modifying
    @Query("DELETE FROM Visit v WHERE v.restaurant.id = :restaurantId")
    void deleteByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Modifying
    @Query("DELETE FROM Visit v WHERE v.restaurant.id IN (SELECT r.id FROM Restaurant r WHERE r.user.id = :userId)")
    void deleteRestaurantVisitorByUserId(@Param("userId") Long userId);
}
