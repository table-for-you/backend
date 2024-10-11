package com.project.tableforyou;

import com.project.tableforyou.common.utils.redis.RedisUtil;
import com.project.tableforyou.domain.reservation.entity.TimeSlot;
import com.project.tableforyou.domain.reservation.service.TimeSlotReservationService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TimeSlotReservationConcurrencyTest {

    @Autowired
    private TimeSlotReservationService timeSlotReservationService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public synchronized void testSaveTimeSlotReservationConcurrency() throws InterruptedException {
        // 스레드 풀 생성 (5개의 스레드)
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Long restaurantId = 1L;
        String baseUsername = "user";
        String date = "2024-10-11";
        TimeSlot timeSlot = TimeSlot.FIVE_PM;  // 테스트용 시간대

        String key = "reservation:" + restaurantId + ":timeslot:" + date + "_" + timeSlot;
        redisUtil.del(key);  // Redis의 기존 데이터 제거

        // 여러 사용자가 동시에 예약을 시도하는 상황을 시뮬레이션
        for (int i = 0; i < 10; i++) {
            String username = baseUsername + i;
            executorService.submit(() -> {
                try {
                    timeSlotReservationService.saveTimeSlotReservation(username, restaurantId, date, timeSlot);
                } catch (Exception e) {
                    System.out.println("에러 발생 " + username + " - " + e.getMessage());
                }
            });
        }

        // 스레드 풀 종료
        executorService.shutdown();
        // 스레드 풀에 남아 있는 작업이 끝날 때까지 최대 1분간 대기
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testVerifyTimeSlotReservation() {
        Long restaurantId = 1L;
        String date = "2024-10-11";
        TimeSlot timeSlot = TimeSlot.FIVE_PM;;  // 테스트용 시간대
        String key = "reservation:" + restaurantId + ":timeslot:" + date + "_" + timeSlot;

        // Redis에서 예약된 모든 데이터를 가져오기
        int reservationCount = redisUtil.hashSize(key);

        // Redis에 저장된 예약 수 확인
        System.out.println("예약된 사용자 수: " + reservationCount);
    }
}
