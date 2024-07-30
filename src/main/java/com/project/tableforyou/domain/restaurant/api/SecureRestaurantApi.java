package com.project.tableforyou.domain.restaurant.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[(권한 필요 o) 가게 API]", description = "권한이 필요한 가게 관련 API")
public interface SecureRestaurantApi {

    @Operation(summary = "가게 평점 업데이트하기 *", description = "가게 평점을 업데이트하는 API입니다.")
    ResponseEntity<?> updateRating(@PathVariable(name = "restaurantId") Long restaurantId,
                                   @RequestParam("rating") Double rating);
}
