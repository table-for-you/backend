package com.project.tableforyou.domain.reservation.api;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[(권한 필요 x) 특정 시간 예약 API]", description = "권한이 필요없는 특정 시간대 예약 관련 API")
public interface PublicTimeSlotReservationApi {

    @Operation(summary = "특정 시간대 예약 다 찼는지 확인하기", description = "특정 시간대에 예약이 다 찼는지 확인하는 API입니다.")
    ResponseEntity<?> checkTimeReservationFull(@PathVariable(name = "restaurantId") Long restaurantId,
                                               @RequestParam(value = "time-slot") TimeSlot timeSlot);
}