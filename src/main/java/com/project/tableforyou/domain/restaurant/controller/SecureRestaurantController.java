package com.project.tableforyou.domain.restaurant.controller;

import com.project.tableforyou.domain.restaurant.api.SecureRestaurantApi;
import com.project.tableforyou.domain.restaurant.service.RestaurantService;
import com.project.tableforyou.common.utils.api.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class SecureRestaurantController implements SecureRestaurantApi {

    private final RestaurantService restaurantService;

    /* 가게 평점 업데이트 */
    @Override
    @PatchMapping("/{restaurantId}/update-rating")
    public ResponseEntity<?> updateRating(@PathVariable(name = "restaurantId") Long restaurantId,
                                          @RequestParam("rating") Double rating) {

        restaurantService.updateRating(restaurantId, rating);
        return ResponseEntity.ok(ApiUtil.from("가게 평점 업데이트 완료."));

    }
}
