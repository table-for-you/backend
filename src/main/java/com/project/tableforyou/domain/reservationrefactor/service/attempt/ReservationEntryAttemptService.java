package com.project.tableforyou.domain.reservationrefactor.service.attempt;

import com.project.tableforyou.domain.reservationrefactor.entity.ReservationEntryAttempt;
import com.project.tableforyou.domain.reservationrefactor.repository.ReservationEntryAttemptRepository;
import com.project.tableforyou.domain.reservationrefactor.type.AttemptStatus;
import com.project.tableforyou.domain.reservationrefactor.type.FailReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationEntryAttemptService {
    private final ReservationEntryAttemptRepository reservationEntryAttemptRepository;

    @Transactional
    public void createInProgress(
            String attemptId,
            Long userId,
            Long restaurantId,
            LocalDate date,
            LocalDateTime expiresAt
    ) {
        ReservationEntryAttempt attempt = ReservationEntryAttempt.builder()
                .attemptId(attemptId)
                .userId(userId)
                .restaurantId(restaurantId)
                .date(date)
                .status(AttemptStatus.IN_PROGRESS)
                .failReason(FailReason.NONE)
                .expiresAt(expiresAt)
                .build();

        reservationEntryAttemptRepository.save(attempt);
    }

    public Optional<ReservationEntryAttempt> readByAttemptId(String attemptId) {
        return reservationEntryAttemptRepository.findByAttemptId(attemptId);
    }

    /**
     * 입장 처리 상태 업데이트
     */
    @Transactional
    public void finishAttempt(String attemptId, AttemptStatus status, FailReason reason) {
        ReservationEntryAttempt attempt = readByAttemptId(attemptId).orElse(null);

        if (attempt == null || attempt.isFinished()) {
            return;
        }

        switch (status) {
            case SUCCESS -> attempt.markSuccess();
            case FAIL -> attempt.markFail(reason);
            case TIMEOUT -> attempt.markTimeout();
            default -> attempt.markFail(FailReason.SYSTEM);
        }
    }

}
