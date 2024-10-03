package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.api.SecureQueueReservationApi;
import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.common.utils.api.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class SecureQueueReservationController implements SecureQueueReservationApi {

    private final QueueReservationService queueReservationService;

    /* 예약자 추가 */
    @Override
    @PostMapping("/{restaurantId}/queue-reservations")
    public ResponseEntity<?> createReservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @PathVariable(name = "restaurantId") Long restaurantId) {

        queueReservationService.saveQueueReservation(principalDetails.getUsername(), restaurantId);
        return ResponseEntity.ok(ApiUtil.from("예약자 추가 성공."));

    }

    /* 예약 순서 미루기 (사용자) */ // restaurant_id 에서 이름을 가져오기. reservation_id에서 booking 가져오기
    @Override
    @PatchMapping("/{restaurantId}/queue-reservations/postponed-guest-booking")
    public ResponseEntity<?> postponedGuestBooking(@PathVariable(name = "restaurantId") Long restaurantId,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                        @RequestBody QueueReservationReqDto reservationDto) {

        List<QueueReservationResDto> decreaseReservation =
                queueReservationService.getQueueReservations(restaurantId, principalDetails.getUsername(), reservationDto);
        queueReservationService.decreaseBooking(decreaseReservation, restaurantId);
        queueReservationService.postponedGuestBooking(restaurantId, principalDetails.getUsername(), reservationDto);
        return ResponseEntity.ok(ApiUtil.from("예약자 미루기 + 앞당기기 성공."));
    }

    /* 예약자 삭제 (사용자) */
    @Override
    @DeleteMapping("/{restaurantId}/queue-reservations")
    public ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<QueueReservationResDto> decreaseReservation =
                queueReservationService.getQueueReservations(restaurantId, principalDetails.getUsername(), null);
        queueReservationService.deleteQueueReservation(restaurantId, principalDetails.getUsername());
        queueReservationService.decreaseBooking(decreaseReservation, restaurantId);
        return ResponseEntity.ok(ApiUtil.from("예약자 삭제 성공."));
    }

    /* 나의 예약 번호 불러오기 */
    @Override
    @GetMapping("/{restaurantId}/queue-reservations/me")
    public ResponseEntity<?> getMyBookingNum(@PathVariable(name = "restaurantId") Long restaurantId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(ApiUtil.from(
                queueReservationService.getMyBooking(
                        restaurantId,
                        principalDetails.getUsername()
                )));
    }
}
