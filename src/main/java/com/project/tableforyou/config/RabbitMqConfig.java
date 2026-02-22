package com.project.tableforyou.config;

import com.project.tableforyou.config.properties.RabbitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    private final RabbitProperties properties;

    @Bean
    public DirectExchange reservationExchange() {
        return new DirectExchange(properties.getExchange().getReservation());
    }

    @Bean
    public DirectExchange timeoutDelayExchange() {
        return new DirectExchange(properties.getExchange().getTimeoutDelay());
    }

    @Bean
    public DirectExchange timeoutExchange() {
        return new DirectExchange(properties.getExchange().getTimeout());
    }

    /**
     * 대기열 입장 이벤트 소비 큐
     */
    @Bean
    public Queue queueJoinedQueue() {
        return QueueBuilder.durable(properties.getQueue().getJoined()).build();
    }

    /**
     * 예약 시도 종료 이벤트 소비 큐
     */
    @Bean
    public Queue attemptFinishedQueue() {
        return QueueBuilder.durable(properties.getQueue().getAttemptFinished()).build();
    }

    /**
     * 2분 TTL 대기용 큐
     * - TTL 만료 후 timeout 처리 큐로 이동
     */
    @Bean
    public Queue attemptTimeoutDelayQueue() {
        return QueueBuilder.durable(properties.getQueue().getTimeoutDelay())
                .withArgument("x-message-ttl", properties.getTtl().getAttempt())
                .withArgument("x-dead-letter-exchange", properties.getExchange().getTimeout())
                .withArgument("x-dead-letter-routing-key", properties.getRouting().getTimeout())
                .build();
    }

    /**
     * TTL 만료 후 실제 timeout 처리 큐
     */
    @Bean
    public Queue attemptTimeoutQueue() {
        return QueueBuilder.durable(properties.getQueue().getTimeout()).build();
    }

    @Bean
    public Binding bindQueueJoined(DirectExchange reservationExchange, Queue queueJoinedQueue) {
        return BindingBuilder.bind(queueJoinedQueue)
                .to(reservationExchange)
                .with(properties.getRouting().getQueueJoined());
    }

    @Bean
    public Binding bindAttemptFinished(DirectExchange reservationExchange, Queue attemptFinishedQueue) {
        return BindingBuilder.bind(attemptFinishedQueue)
                .to(reservationExchange)
                .with(properties.getRouting().getAttemptFinished());
    }

    @Bean
    public Binding bindTimeoutDelay(DirectExchange timeoutDelayExchange, Queue attemptTimeoutDelayQueue) {
        return BindingBuilder.bind(attemptTimeoutDelayQueue)
                .to(timeoutDelayExchange)
                .with(properties.getRouting().getTimeoutDelay());
    }

    @Bean
    public Binding bindTimeout(DirectExchange timeoutExchange, Queue attemptTimeoutQueue) {
        return BindingBuilder.bind(attemptTimeoutQueue)
                .to(timeoutExchange)
                .with(properties.getRouting().getTimeout());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
