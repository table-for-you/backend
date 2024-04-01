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
@RequestMapping("/restaurant")
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    /* 예약자 추가 */
    @PostMapping("/{restaurant}/reservation/create")
    public ResponseEntity<String> create(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @PathVariable(name = "restaurant") String restaurant) {

        reservationService.save(principalDetails.getUsername(), restaurant);
        return ResponseEntity.ok("예약자 추가 성공.");

    }

    /* 예약자 읽기 */
    @GetMapping("/{restaurant}/reservation/{username}")
    public ReservationResponseDto read(@PathVariable(name = "restaurant") String restaurant,
                                       @PathVariable(name = "username") String username) {
        return reservationService.findByBooking(restaurant, username);
    }

    /* 해당 가게 예약자 불러오기. */
    @GetMapping("/{restaurant}/reservation")
    public List<ReservationResponseDto> readAll(@PathVariable(name = "restaurant") String restaurant) {
        return reservationService.findAllReservation(restaurant);
    }

    /* 예약자 앞으로 당기기 */
    @PatchMapping("/{restaurant}/reservation/decreaseBooking")
    public ResponseEntity<String> decreaseBooking(@PathVariable(name = "restaurant") String restaurant) {
        try {
            List<ReservationResponseDto> reservations = reservationService.getReservations(restaurant, null, null);  // 이미 여기서 트랜잭션은 끝나 1차캐시에 없음.
            String user = reservationService.decreaseBooking(reservations);
            return ResponseEntity.ok(user + "님 입장");
        } catch (Exception e) {
            log.error("Failed to update reservations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약자 업데이트 실패");
        }
    }

    /* 예약 순서 미루기 */ // restaurant_id 에서 이름을 가져오기. reservation_id에서 booking 가져오기
    @PutMapping("/{restaurant}/reservation/postponedGuestBooking/{username}")
    public ResponseEntity<String> postponedGuestBooking(@PathVariable(name = "restaurant") String restaurant,
                                                        @PathVariable(name = "username") String username,
                                                        @RequestBody ReservationRequestDto dto) {

        List<ReservationResponseDto> decreaseReservation = reservationService.getReservations(restaurant, username, dto);
        reservationService.decreaseBooking(decreaseReservation);
        reservationService.postponedGuestBooking(restaurant, username, dto);
        return ResponseEntity.ok("예약자 미루기 + 앞당기기 성공.");
    }

    /* 예약자 삭제 */
    @DeleteMapping("/{restaurant}/reservation/{username}")
    public ResponseEntity<String> delete(@PathVariable(name = "restaurant") String restaurant,
                                         @PathVariable(name = "username") String username) {

        List<ReservationResponseDto> decreaseReservation = reservationService.getReservations(restaurant, username, null);
        reservationService.decreaseBooking(decreaseReservation);
        reservationService.delete(restaurant, username);
        return ResponseEntity.ok("예약자 삭제 성공.");
    }
}
