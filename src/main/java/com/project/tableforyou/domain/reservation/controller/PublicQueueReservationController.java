package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.domain.visit.service.VisitService;
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
public class PublicQueueReservationController {

    private final QueueReservationService queueReservationService;
    private final VisitService visitService;

    /* 예약자 앞으로 당기기 */
    @PatchMapping("/{restaurantId}/queue-reservations/decrease-booking")
    public ResponseEntity<String> decreaseBooking(@PathVariable(name = "restaurantId") Long restaurantId) {
        try {
            List<QueueReservationResDto> reservations = queueReservationService.getQueueReservations(restaurantId, null, null);
            String username = queueReservationService.decreaseBooking(reservations, restaurantId);

            visitService.saveVisitRestaurant(username, restaurantId);   // 사용자가 방문 가게 목록에 저장
            return ResponseEntity.ok(username + "님 입장");
        } catch (Exception e) {
            log.error("Failed to update reservations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약자 업데이트 실패");
        }
    }
}
