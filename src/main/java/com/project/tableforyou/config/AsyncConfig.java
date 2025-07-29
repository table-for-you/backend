package com.project.tableforyou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean("queueNotificationExecutor")
    public Executor queueNotificationExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();    // CPU 코어 수

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processors * 2);        // 코어 수 x 2 (IO 작업)
        executor.setMaxPoolSize(processors * 3);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("queue-");
        executor.initialize();
        return executor;
    }
}
