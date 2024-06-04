package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.dto.TimeSlotReservationResDto;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.service.OwnerReservationService;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import com.project.tableforyou.domain.reservation.service.TimeSlotReservationService;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.restaurant.service.OwnerRestaurantService;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.utils.api.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/owner/restaurants")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final OwnerRestaurantService ownerRestaurantService;
    private final QueueReservationService queueReservationService;
    private final TimeSlotReservationService timeSlotReservationService;
    private final OwnerReservationService ownerReservationService;

    /* 가게 생성 */
    @PostMapping
    public ResponseEntity<?> createRestaurant(@Valid @RequestBody RestaurantRequestDto dto,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {


        return ResponseEntity.ok(ApiUtil.from(ownerRestaurantService.saveRestaurant(principalDetails.getUsername(), dto)));
    }

    /* 사장 가게 불러오기 */
    @GetMapping
    public List<RestaurantNameDto> readRestaurant(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ownerRestaurantService.findByRestaurantOwner(principalDetails.getUsername());
    }

    /* 가게 업데이트 */
    @PutMapping("/{restaurantId}")
    public ResponseEntity<?> updateRestaurant(@Valid @RequestBody RestaurantUpdateDto restaurantUpdateDto,
                                                   @PathVariable(name = "restaurantId") Long restaurantId) {

        ownerRestaurantService.updateRestaurant(restaurantId, restaurantUpdateDto);
        return ResponseEntity.ok(ApiUtil.from("가게 수정 완료."));
    }


    /* 가게 삭제 */
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable(name = "restaurantId") Long restaurantId) {

        ownerRestaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok(ApiUtil.from("가게 삭제 완료."));

    }

    /* 해당 가게 예약자 불러오기. (번호표) */
    @GetMapping("/{restaurantId}/queue-reservations")
    public List<QueueReservationResDto> readAllRestaurant(@PathVariable(name = "restaurantId") Long restaurantId,
                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (ownerReservationService.isOwnerRestaurant(restaurantId, principalDetails.getUsername()))
            return queueReservationService.findAllQueueReservations(restaurantId);
        else
            throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    /* 예약 순서 미루기 (번호표) */
    @PutMapping("/{restaurantId}/queue-reservations/postponed-guest-booking/{username}")
    public ResponseEntity<?> postponedGuestBooking(@PathVariable(name = "restaurantId") Long restaurantId,
                                                        @PathVariable(name = "username") String username,
                                                        @RequestBody QueueReservationReqDto reservationDto,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {

        if (ownerReservationService.isOwnerRestaurant(restaurantId, principalDetails.getUsername())) {
            List<QueueReservationResDto> decreaseReservation =
                    queueReservationService.getQueueReservations(restaurantId, username, reservationDto);
            queueReservationService.decreaseBooking(decreaseReservation, restaurantId);
            queueReservationService.postponedGuestBooking(restaurantId, username, reservationDto);
            return ResponseEntity.ok(ApiUtil.from("예약자 미루기 + 앞당기기 성공."));
        } else
            throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    /* 예약자 삭제 (번호표) */
    @DeleteMapping("/{restaurantId}/queue-reservations/{username}")
    public ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                    @PathVariable(name = "username") String username,
                                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {

        if (ownerReservationService.isOwnerRestaurant(restaurantId, principalDetails.getUsername())) {
            List<QueueReservationResDto> decreaseReservation =
                    queueReservationService.getQueueReservations(restaurantId, username, null);
            queueReservationService.deleteQueueReservation(restaurantId, username);
            queueReservationService.decreaseBooking(decreaseReservation, restaurantId);
            return ResponseEntity.ok(ApiUtil.from("예약자 삭제 성공."));
        } else
            throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    /* 특정 시간 예약자 전체 불러오기 (특정 시간) */
    @GetMapping("/{restaurantId}/timeslot-reservations")
    public List<TimeSlotReservationResDto> readAllTimeSlotReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                                      @RequestParam(value = "time-slot") TimeSlot timeSlot,
                                                                      @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (ownerReservationService.isOwnerRestaurant(restaurantId, principalDetails.getUsername()))
            return timeSlotReservationService.findAllTimeSlotReservations(restaurantId, timeSlot);
        else
            throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    /* 예약 삭제하기 (특정 시간)*/
    @DeleteMapping("/{restaurantId}/timeslot-reservations/{username}")
    public ResponseEntity<?> deleteReservation(@PathVariable(name = "restaurantId") Long restaurantId,
                                                    @PathVariable(name = "username") String username,
                                                    @RequestParam(value = "time-slot") TimeSlot timeSlot,
                                                    @AuthenticationPrincipal PrincipalDetails principalDetails) {

        if (ownerReservationService.isOwnerRestaurant(restaurantId, principalDetails.getUsername())) {
            timeSlotReservationService.deleteTimeSlotReservation(restaurantId, username, timeSlot);
            return ResponseEntity.ok(ApiUtil.from("예약자 삭제 성공."));
        } else
            throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
