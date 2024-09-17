package com.project.tableforyou.domain.image.repository;

import com.project.tableforyou.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    void deleteByUrl(String url);

    @Query("SELECT i.url FROM Image i WHERE i.restaurant.id = :restaurantId")
    List<String> findImageUrlsByRestaurantId(@Param("restaurantId") Long restaurantId);
}
