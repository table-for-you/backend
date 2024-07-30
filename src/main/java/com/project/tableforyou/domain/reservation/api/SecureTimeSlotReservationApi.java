package com.project.tableforyou.domain.reservation.api;

import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[(권한 필요 o) 특정 시간 예약 API]", description = "권한이 필요한 특정 시간대 예약 관련 API")
public interface SecureTimeSlotReservationApi {

    @Operation(summary = "예약자 추가하기 (특정 시간) *", description = "특정 시간대에 대해 예약을 추가하는 API입니다.")
    ResponseEntity<?> saveReservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      @PathVariable(name = "restaurantId") Long restaurantId,
                                      @RequestParam(value = "time-slot") TimeSlot timeSlot);

    @Operation(summary = "가게에 대해 예약을 했는지 확인하기 (특정 시간) *", description = "특정 시간대에 대해서 가게에 대해 예약을 했는지 확인하는 API입니다.")
    ResponseEntity<?> checkUserReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails,
                                           @RequestParam(value = "time-slot") TimeSlot timeSlot);

    @Operation(summary = "예약자 삭제하기 (특정 시간) *", description = "(예약자 직접) 특정 시간에 대해 예약을 삭제하는 API입니다.")
    ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @RequestParam(value = "time-slot") TimeSlot timeSlot);
}
