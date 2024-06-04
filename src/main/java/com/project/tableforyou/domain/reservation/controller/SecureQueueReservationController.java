package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
@Slf4j
public class SecureQueueReservationController {

    private final QueueReservationService queueReservationService;

    /* 예약자 추가 */
    @PostMapping("/{restaurantId}/queue-reservations")
    public ResponseEntity<String> createReservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @PathVariable(name = "restaurantId") Long restaurantId) {

        queueReservationService.saveQueueReservation(principalDetails.getUsername(), restaurantId);
        return ResponseEntity.ok("예약자 추가 성공.");

    }

    /* 가게에 대해 예약을 했는지 확인 */
    @GetMapping("/{restaurantId}/queue-reservations-check")
    public ResponseEntity<Boolean> checkUserReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {

        boolean isReserved = queueReservationService.isUserAlreadyInQueue(principalDetails.getUsername(), restaurantId);
        return ResponseEntity.ok(isReserved);
    }

    /* 예약 순서 미루기 (사용자) */ // restaurant_id 에서 이름을 가져오기. reservation_id에서 booking 가져오기
    @PutMapping("/{restaurantId}/queue-reservations/postponed-guest-booking")
    public ResponseEntity<String> postponedGuestBooking(@PathVariable(name = "restaurantId") Long restaurantId,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                        @RequestBody QueueReservationReqDto reservationDto) {

        List<QueueReservationResDto> decreaseReservation =
                queueReservationService.getQueueReservations(restaurantId, principalDetails.getUsername(), reservationDto);
        queueReservationService.decreaseBooking(decreaseReservation, restaurantId);
        queueReservationService.postponedGuestBooking(restaurantId, principalDetails.getUsername(), reservationDto);
        return ResponseEntity.ok("예약자 미루기 + 앞당기기 성공.");
    }

    /* 예약자 삭제 (사용자) */
    @DeleteMapping("/{restaurantId}/queue-reservations")
    public ResponseEntity<String> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<QueueReservationResDto> decreaseReservation =
                queueReservationService.getQueueReservations(restaurantId, principalDetails.getUsername(), null);
        queueReservationService.deleteQueueReservation(restaurantId, principalDetails.getUsername());
        queueReservationService.decreaseBooking(decreaseReservation, restaurantId);
        return ResponseEntity.ok("예약자 삭제 성공.");
    }
}
