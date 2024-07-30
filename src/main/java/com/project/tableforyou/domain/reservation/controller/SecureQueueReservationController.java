package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.api.SecureQueueReservationApi;
import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.utils.api.ApiUtil;
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

    /* 가게에 대해 예약을 했는지 확인 */
    @Override
    @GetMapping("/{restaurantId}/queue-reservations-check")
    public ResponseEntity<?> checkUserReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(ApiUtil.from(queueReservationService.isUserAlreadyInQueue(principalDetails.getUsername(), restaurantId)));
    }

    /* 예약 순서 미루기 (사용자) */ // restaurant_id 에서 이름을 가져오기. reservation_id에서 booking 가져오기
    @Override
    @PutMapping("/{restaurantId}/queue-reservations/postponed-guest-booking")
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
}
