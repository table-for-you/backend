package com.project.tableforyou.domain.reservation.service;

import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.common.utils.api.ApiUtil;
import com.project.tableforyou.common.utils.redis.RedisUtil;
import com.project.tableforyou.domain.reservation.dto.QueueReservationReqDto;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.dto.TimeSlotReservationResDto;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.restaurant.service.RestaurantService;
import com.project.tableforyou.domain.visit.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.project.tableforyou.common.utils.redis.RedisProperties.RESERVATION_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class OwnerReservationFacade {

    private final RestaurantService restaurantService;
    private final QueueReservationService queueReservationService;
    private final TimeSlotReservationService timeSlotReservationService;
    private final RedisUtil redisUtil;
    private final VisitService visitService;

    public String updateUsedSeats(Long restaurantId, boolean increase) {

        int value = increase ? 1 : -1;

        String key = RESERVATION_KEY_PREFIX + "queue:" + restaurantId;

        if (value == -1 && redisUtil.hashSize(key) != 0) {   // 좌석이 다 차서 예약자에서 인원을 가져올 때. (인원이 줄면) redis값을 가져와 있는지 확인한 후 보내기
            List<QueueReservationResDto> reservations =
                    queueReservationService.getQueueReservations(restaurantId, null, null);
            String username = queueReservationService.decreaseBooking(reservations, restaurantId);

            visitService.saveVisitRestaurant(username, restaurantId);   // 사용자가 방문 가게 목록에 저장

            return username + "님 입장";
        } else {                                                          // 좌석이 덜 찼을 때
            restaurantService.updateUsedSeats(restaurantId, value);

            return "가게 좌석 변경 성공.";
        }

    }

    public List<QueueReservationResDto> getQueueReservations(Long restaurantId) {
        return queueReservationService.findAllQueueReservations(restaurantId);
    }

    public void postponeReservation(Long restaurantId, String username, QueueReservationReqDto reservationDto) {
        List<QueueReservationResDto> decreaseReservation =
                queueReservationService.getQueueReservations(restaurantId, username, reservationDto);
        queueReservationService.decreaseBooking(decreaseReservation, restaurantId);
        queueReservationService.postponedGuestBooking(restaurantId, username, reservationDto);
    }

    public void deleteQueueReservation(Long restaurantId, String username) {
        List<QueueReservationResDto> decreaseReservation =
                queueReservationService.getQueueReservations(restaurantId, username, null);
        queueReservationService.deleteQueueReservation(restaurantId, username);
        queueReservationService.decreaseBooking(decreaseReservation, restaurantId);
    }

    public List<TimeSlotReservationResDto> getTimeSlotReservations(Long restaurantId, String date, TimeSlot timeSlot) {
        return timeSlotReservationService.findAllTimeSlotReservations(restaurantId, date, timeSlot);
    }

    public void deleteTimeSlotReservation(Long restaurantId, String username, String date, TimeSlot timeSlot) {
        timeSlotReservationService.deleteTimeSlotReservation(restaurantId, username, date, timeSlot);
    }
}
