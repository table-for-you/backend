package com.project.tableforyou.domain.reservationrefactor.redis.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeUtil {

    public static long getExpireSeconds() {
        return Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()).getSeconds();
    }
}
