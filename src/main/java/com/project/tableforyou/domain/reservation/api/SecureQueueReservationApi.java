package com.project.tableforyou.domain.reservation.api;

import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[(권한 필요 o) 번호표 예약 API]", description = "권한이 필요한 번호표 예약 관련 API")
public interface SecureQueueReservationApi {

    @Operation(summary = "예약자 추가하기 (번호표) *", description = "번호표에 대한 예약자를 추가하는 API입니다.")
    ResponseEntity<?> createReservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @PathVariable(name = "restaurantId") Long restaurantId);

    @Operation(summary = "가게에 대해 예약을 했는지 확인하기 (번호표) *", description = "번호표에 대해서 가게에 예약을 했는지 확인하는 API입니다.")
    ResponseEntity<?> checkUserReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "예약 순서 미루기 (번호표) *", description = "(예약자 직접) 번호표에 대해 예약 순서 미루는 API입니다.")
    ResponseEntity<?> postponedGuestBooking(@PathVariable(name = "restaurantId") Long restaurantId,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails,
                                            @RequestBody QueueReservationReqDto reservationDto);

    @Operation(summary = "예약자 삭제하기 (번호표) *", description = "(예약자 직접) 번호표에 대해 예약을 삭제하는 API입니다.")
    ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails);
}
