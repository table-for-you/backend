package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.domain.visit.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/restaurants")
@Slf4j
public class PublicQueueReservationController {

    private final QueueReservationService queueReservationService;
    private final VisitService visitService;

    /* 예약자 앞으로 당기기 */
    @PatchMapping("/{restaurantId}/queue-reservations/decrease-booking")
    public ResponseEntity<Map<String, String>> decreaseBooking(@PathVariable(name = "restaurantId") Long restaurantId) {

        List<QueueReservationResDto> reservations = queueReservationService.getQueueReservations(restaurantId, null, null);
        String username = queueReservationService.decreaseBooking(reservations, restaurantId);

        visitService.saveVisitRestaurant(username, restaurantId);   // 사용자가 방문 가게 목록에 저장
        Map<String, String> response = new HashMap<>();
        response.put("message", username + "님 입장");
        return ResponseEntity.ok(response);
    }
}
