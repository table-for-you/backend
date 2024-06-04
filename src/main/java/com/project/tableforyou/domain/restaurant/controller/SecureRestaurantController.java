package com.project.tableforyou.domain.restaurant.controller;

import com.project.tableforyou.domain.restaurant.service.RestaurantService;
import com.project.tableforyou.utils.api.ApiUtil;
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
    public ResponseEntity<?> updateRating(@PathVariable(name = "restaurantId") Long restaurantId,
                                          @RequestParam("rating") Double rating) {

        restaurantService.updateRating(restaurantId, rating);
        return ResponseEntity.ok(ApiUtil.from("가게 평점 업데이트 완료."));

    }
}
