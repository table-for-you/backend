package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.dto.ReservationResponseDto;
import com.project.tableforyou.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/restaurants")
@Slf4j
public class PublicReservationController {

    private final ReservationService reservationService;

    /* 예약자 읽기 */
    @GetMapping("/{restaurantId}/reservations/{username}")
    public ReservationResponseDto read(@PathVariable(name = "restaurantId") Long restaurantId,
                                       @PathVariable(name = "username") String username) {
        return reservationService.findByBooking(restaurantId, username);
    }


    /* 예약자 앞으로 당기기 */
    @PatchMapping("/{restaurantId}/reservations/decrease-booking")
    public ResponseEntity<String> decreaseBooking(@PathVariable(name = "restaurantId") Long restaurantId) {
        try {
            List<ReservationResponseDto> reservations = reservationService.getReservations(restaurantId, null, null);
            String user = reservationService.decreaseBooking(reservations, restaurantId);
            return ResponseEntity.ok(user + "님 입장");
        } catch (Exception e) {
            log.error("Failed to update reservations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약자 업데이트 실패");
        }
    }
}
