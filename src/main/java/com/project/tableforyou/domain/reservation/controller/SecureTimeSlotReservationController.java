package com.project.tableforyou.domain.reservation.controller;

import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.dto.TimeSlotReservationResDto;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.service.TimeSlotReservationService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.utils.api.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class SecureTimeSlotReservationController {

    private final TimeSlotReservationService timeSlotReservationService;

    /* 특정 시간대 예약하기 */
    @PostMapping("/{restaurantId}/timeslot-reservations")
    public ResponseEntity<?> saveReservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                          @PathVariable(name = "restaurantId") Long restaurantId,
                                                          @RequestParam(value = "time-slot") TimeSlot timeSlot) {

        timeSlotReservationService.saveTimeSlotReservation(principalDetails.getUsername(), restaurantId, timeSlot);
        return ResponseEntity.ok(ApiUtil.from("예약자 추가 성공."));
    }

    /* 예약을 했는지 확인 */
    @GetMapping("/{restaurantId}/timeslot-reservations-check")
    public ResponseEntity<?> checkUserReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                        @RequestParam(value = "time-slot") TimeSlot timeSlot) {


        return ResponseEntity.ok(ApiUtil.from(timeSlotReservationService
                .isUserAlreadyInTimeSlot(principalDetails.getUsername(), restaurantId, timeSlot)));
    }

    /* 예약 삭제하기 */
    @DeleteMapping("/{restaurantId}/timeslot-reservations")
    public ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                    @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestParam(value = "time-slot") TimeSlot timeSlot) {

        timeSlotReservationService.deleteTimeSlotReservation(restaurantId, principalDetails.getUsername(), timeSlot);
        return ResponseEntity.ok(ApiUtil.from("예약자 삭제 성공."));
    }

}
