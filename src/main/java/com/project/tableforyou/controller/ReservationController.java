package com.project.tableforyou.controller;

import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.domain.dto.ReservationDto;
import com.project.tableforyou.domain.entity.Reservation;
import com.project.tableforyou.domain.entity.Restaurant;
import com.project.tableforyou.domain.entity.User;
import com.project.tableforyou.service.ReservationService;
import com.project.tableforyou.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class ReservationController {

    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    /* 예약자 추가 */
    @PostMapping("/{restaurant_id}/reservation/create")
    public ResponseEntity<String> create(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @PathVariable Long restaurant_id) {
        try {
            reservationService.save(principalDetails.getUser().getId(), restaurant_id);
            return ResponseEntity.ok("예약자 추가 성공.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약자 추가 실패.");
        }
    }

    /* 예약자 읽기 */
    @GetMapping("/reservation/{reservation_id}")
    public ReservationDto.Response read(@PathVariable Long reservation_id) {
        return reservationService.findById(reservation_id);
    }

    /* 해당 가게 예약자 불러오기. 페이징 처리 */
    @GetMapping("/{restaurant_id}/reservation")
    public Page<ReservationDto.Response> readAll(@PageableDefault(size = 50, sort = "booking", direction = Sort.Direction.ASC) Pageable pageable,
                                                 @PathVariable Long restaurant_id) {
        return reservationService.findByRestaurantId(restaurant_id,pageable);
    }

    /* 예약자 앞으로 당기기 */
    @PatchMapping("/{restaurant_id}/reservation/decreaseBooking")
    public ResponseEntity<String> decreaseBooking(@PathVariable Long restaurant_id) {
        try {
            List<Reservation> reservations = reservationService.getReservations(restaurant_id, 0L, null);  // 이미 여기서 트랜잭션은 끝나 1차캐시에 없음.
            User user = reservationService.decreaseBooking(reservations);
            return ResponseEntity.ok(user.getNickname() + "님 입장");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약자 업데이트 실패");
        }
    }

    /* 예약 순서 미루기 */
    @PutMapping("/{restaurant_id}/reservation/postponedGuestBooking/{reservation_id}")
    public ResponseEntity<String> postponedGuestBooking(@PathVariable Long restaurant_id, @PathVariable Long reservation_id,
                                                        @RequestBody ReservationDto.Request dto) {

        try {
            List<Reservation> decreaseReservation = reservationService.getReservations(restaurant_id, reservation_id, dto);

            reservationService.decreaseBooking(decreaseReservation);

            reservationService.postponedGuestBooking(reservation_id, dto);

            return ResponseEntity.ok("예약자 미루기 + 앞당기기 성공.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약자 미루기 실패.");
        }
    }

    /* 예약자 삭제 */
    @DeleteMapping("/{restaurant_id}/reservation/{reservation_id}")
    public ResponseEntity<String> delete(@PathVariable Long reservation_id, @PathVariable Long restaurant_id) {
        try {
            List<Reservation> decreaseReservation = reservationService.getReservations(restaurant_id, reservation_id, null);
            reservationService.decreaseBooking(decreaseReservation);
            reservationService.delete(reservation_id);
            return ResponseEntity.ok("예약자 삭제 성공.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약자 삭제 실패");
        }
    }
}
