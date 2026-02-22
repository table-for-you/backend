package com.project.tableforyou.domain.reservationrefactor.redis.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class QueueAdmissionRedisService {
    private final StringRedisTemplate redis;

    private static final String PREFIX = "qr:";

    private String queueKey(Long restaurantId) {
        return PREFIX + "queue:" + restaurantId;
    }

    private String permitKey(Long restaurantId) {
        return PREFIX + "permit:" + restaurantId;
    }

    private String doneKey(String attemptId) {
        return PREFIX + "done:" + attemptId;
    }

    /**
     * 특정 restaurantId의 대기열에 사용자를 추가
     * - ZSET을 사용, score는 현재 시간으로 저장
     *
     * @param restaurantId 대기열 대상 식당 ID
     * @param userId       대기열에 등록할 사용자 ID
     */
    public void enqueue(Long restaurantId, Long userId) {
        redis.opsForZSet().add(queueKey(restaurantId), String.valueOf(userId), System.currentTimeMillis());
    }

    /**
     * 해당 restaurantId의 현재 대기열 인원 수를 반환
     *
     * @param restaurantId 식당 ID
     * @return 대기열 인원 수 (없으면 0)
     */
    public long getQueueSize(Long restaurantId) {
        Long c = redis.opsForZSet().zCard(queueKey(restaurantId));
        return c == null ? 0 : c;
    }

    /**
     * 해당 restaurantId 대기열에서 특정 사용자의 순번 반환
     *
     * @param restaurantId 식당 ID
     * @param userId       사용자 ID
     * @return 순번
     */
    public Long getRank(Long restaurantId, Long userId) {
        return redis.opsForZSet().rank(queueKey(restaurantId), String.valueOf(userId));
    }

    /**
     * 해당 restaurantId 대기열에서 가장 먼저 대기 중인 사용자를 꺼내고 제거
     * - Redis의 popMin을 사용하여 score가 가장 작은 사용자(가장 오래 기다린 사용자)를 원자적으로 조회 및 제거
     *
     * @param restaurantId 식당 ID
     * @return 사용자 ID, 대기열이 비어있으면 null
     */
    public Long popNextUser(Long restaurantId) {
        ZSetOperations<String, String> z = redis.opsForZSet();
        ZSetOperations.TypedTuple<String> t = z.popMin(queueKey(restaurantId));
        if (t == null || t.getValue() == null) return null;
        return Long.valueOf(t.getValue());
    }

    /**
     * 해당 restaurantId의 입장 가능 인원을 1 증가
     * - 현재 permit 값이 maxActive를 초과할 경우 즉시 롤백하고 false 반환
     *
     * @param restaurantId 식당 ID
     * @param maxActive    허용 가능한 최대 동시 입장 수
     * @return permit 획득 성공 여부
     */
    public boolean tryAcquirePermit(Long restaurantId, int maxActive) {
        Long v = redis.opsForValue().increment(permitKey(restaurantId));
        if (v == null) return false;

        if (v > maxActive) {
            redis.opsForValue().decrement(permitKey(restaurantId));
            return false;
        }
        return true;
    }

    /**
     * 해당 restaurantId의 입장 가능 인원을 1 감소
     * - 음수로 내려가는 것을 방지하기 위해 0 미만일 경우 0으로 설정
     *
     * @param restaurantId 식당 ID
     */
    public void releasePermit(Long restaurantId) {
        Long v = redis.opsForValue().decrement(permitKey(restaurantId));
        if (v != null && v < 0) redis.opsForValue().set(permitKey(restaurantId), "0");
    }

    /**
     * 특정 attemptId 처리 완료 기록
     * - 이벤트 중복 처리를 방지
     *
     * @param attemptId attempt 식별자
     * @return 최초 처리 여부 (true: 최초, false: 이미 처리됨)
     */
    public boolean markDoneOnce(String attemptId) {
        Boolean first = redis.opsForValue().setIfAbsent(doneKey(attemptId), "1", Duration.ofDays(1));
        return Boolean.TRUE.equals(first);
    }

    /**
     * 특정 attemptId가 이미 처리 완료되었는지 확인
     *
     * @param attemptId attempt 식별자
     * @return 처리 완료 여부
     */
    public boolean isDone(String attemptId) {
        return Boolean.TRUE.equals(redis.hasKey(doneKey(attemptId)));
    }
}
