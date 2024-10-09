package com.project.tableforyou;

import com.project.tableforyou.common.utils.redis.RedisUtil;
import com.project.tableforyou.domain.reservation.dto.QueueReservationResDto;
import com.project.tableforyou.domain.reservation.entity.QueueReservation;
import com.project.tableforyou.domain.reservation.service.QueueReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class QueueReservationConcurrencyTest {

    @Autowired
    private QueueReservationService queueReservationService;

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public synchronized void testSaveQueueReservationConcurrency() throws InterruptedException {
        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(5); // 스레드 5개 지정
        Long restaurantId = 1L;
        String baseUsername = "user";

        String key = "reservation:queue:" + restaurantId;
        redisUtil.del(key);     // 기존 값 제거

        // 여러 사용자가 동시에 예약을 추가하는 것을 시뮬레이션
        for (int i = 0; i < 10; i++) {  // 100개의 예약 요청을 동시에 실행
            String username = baseUsername + i;
            executorService.submit(() -> {
                try {
                    queueReservationService.saveQueueReservation(username, restaurantId);
                } catch (Exception e) {
                    System.out.println("에러 발생 " + username + " - " + e.getMessage());
                }
            });
        }

        // 스레드 풀 종료
        executorService.shutdown();
        // 스레드 풀에 남아 있는 작업 끝날 때 까지 대기 (최대 1분)
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testVerifyQueueReservation() {
        Long restaurantId = 1L;
        String key = "reservation:queue:" + restaurantId;

        // Redis에서 예약된 모든 데이터를 리스트로 가져오기
        List<QueueReservationResDto> reservations = redisUtil.getQueueEntries(key);

        // Redis에 저장된 예약 번호 및 사용자 확인
        reservations.forEach(queueReservation -> {
            System.out.println("사용자: " + queueReservation.getUsername() +
                    ", 예약 번호: " + queueReservation.getBooking());
        });
    }
}