package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.dto.ReservationRequestDto;
import com.project.tableforyou.domain.reservation.dto.ReservationResponseDto;
import com.project.tableforyou.domain.reservation.service.ReservationService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
@Slf4j
public class SecureReservationController {

    private final ReservationService reservationService;

    /* 예약자 추가 */
    @PostMapping("/{restaurantId}/reservations")
    public ResponseEntity<String> create(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @PathVariable(name = "restaurantId") Long restaurantId) {

        reservationService.save(principalDetails.getUsername(), restaurantId);
        return ResponseEntity.ok("예약자 추가 성공.");

    }


    /* 예약 순서 미루기 */ // restaurant_id 에서 이름을 가져오기. reservation_id에서 booking 가져오기
    @PutMapping("/{restaurantId}/reservations/postponed-guest-booking/{username}")
    public ResponseEntity<String> postponedGuestBooking(@PathVariable(name = "restaurantId") Long restaurantId,
                                                        @PathVariable(name = "username") String username,
                                                        @RequestBody ReservationRequestDto dto) {

        List<ReservationResponseDto> decreaseReservation = reservationService.getReservations(restaurantId, username, dto);
        reservationService.decreaseBooking(decreaseReservation, restaurantId);
        reservationService.postponedGuestBooking(restaurantId, username, dto);
        return ResponseEntity.ok("예약자 미루기 + 앞당기기 성공.");
    }

    /* 예약자 삭제 */
    @DeleteMapping("/{restaurantId}/reservations/{username}")
    public ResponseEntity<String> delete(@PathVariable(name = "restaurantId") Long restaurantId,
                                         @PathVariable(name = "username") String username) {

        List<ReservationResponseDto> decreaseReservation = reservationService.getReservations(restaurantId, username, null);
        reservationService.decreaseBooking(decreaseReservation, restaurantId);
        reservationService.delete(restaurantId, username);
        return ResponseEntity.ok("예약자 삭제 성공.");
    }
}
