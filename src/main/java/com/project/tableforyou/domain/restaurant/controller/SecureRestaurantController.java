package com.project.tableforyou.domain.restaurant.controller;

import com.project.tableforyou.domain.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Slf4j
public class SecureRestaurantController {

    private final RestaurantService restaurantService;

    /* 가게 평점 업데이트*/
    @PatchMapping("/{restaurantId}/update-rating")
    public ResponseEntity<String> updateRating(@PathVariable(name = "restaurantId") Long restaurantId, @RequestParam("rating") Double rating) {
        try {
            restaurantService.updateRating(restaurantId, rating);
            return ResponseEntity.ok("가게 평점 업데이트 완료.");
        } catch (Exception e) {
            log.error("Error occurred while updating restaurant rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 평점 업데이트 실패.");
        }
    }
}
