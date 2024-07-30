package com.project.tableforyou.domain.reservation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[(권한 필요 x) 번호표 예약 API]", description = "권한이 필요없는 번호표 예약 관련 API")
public interface PublicQueueReservationApi {

    @Operation(summary = "예약자 앞으로 당기기 (실제 호출 x)", description = "예약자를 앞으로 당기는 API입니다." +
            "<br>실제 호출하지 않고, 좌석 업데이트(/public/restaurants/{restaurantId}/update-used-seats)에 의해 자동 호출됩니다.")
    ResponseEntity<?> decreaseBooking(@PathVariable(name = "restaurantId") Long restaurantId);
}
