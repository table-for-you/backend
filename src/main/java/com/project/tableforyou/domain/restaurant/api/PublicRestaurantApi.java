package com.project.tableforyou.domain.restaurant.api;

import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Tag(name = "[(권한 필요 x) 가게 API]", description = "권한이 필요없는 가게 관련 API")
public interface PublicRestaurantApi {

    @Operation(summary = "특정 가게 불러오기", description = "특정 가게를 불러오는 API입니다.")
    RestaurantResponseDto readRestaurant(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "전체 가게 불러오기", description = "전체 가게를 불러오는 API입니다.")
    ResponseEntity<?> readAllRestaurant(
            @PageableDefault(size = 20, sort = "rating", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false, value = "type") String type,
            @RequestParam(required = false, value = "search-keyword") String searchKeyword,
            @RequestParam(required = false, value = "sort-by", defaultValue = "rating") String sortBy,
            @RequestParam(required = false, value = "direction", defaultValue = "DESC") String direction);

    @Operation(summary = "현재 가게 예약자 수 불러오기 (번호표)", description = "번호표에 대한 현재 가게의 예약자 수를 불러오는 API입니다.")
    ResponseEntity<?> waiting(@PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "좌석 업데이트", description = "가게 좌석을 업데이트 API입니다." +
                                        "<br>좌석 증가시 increase는 true, 감소시 increase는 false." +
    "이 외는 서버에서 처리합니다. (가게 좌석 증가 + 가게 다 찼을 시, 번호표 예약으로 자동 이동, 가게 좌석 감소 + 가게 다 찼을 시, 다음 예약 순번 자동으로 불러오기")
    void updateFullUsedSeats(@PathVariable(name = "restaurantId") Long restaurantId,
                             @RequestParam("increase") boolean increase,
                             HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    @Operation(summary = "좌석 업데이트 (실제 호출 x)", description = "좌석 업데이트를 하는 API입니다." +
    "<br>실제 호출하지 않고, 좌석 업데이트(/public/restaurants/{restaurantId}/update-used-seats)에 의해 자동 호출됩니다.")
    ResponseEntity<?> updateUsedSeats(@PathVariable(name = "restaurantId") Long restaurantId,
                                      @PathVariable(name = "value") int value);
}
