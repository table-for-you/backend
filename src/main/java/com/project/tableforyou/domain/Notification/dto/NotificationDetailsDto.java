package com.project.tableforyou.domain.Notification.dto;

import com.project.tableforyou.domain.Notification.entity.Notification;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationDetailsDto {
    // 추가해라
    private final String content;
    private final RestaurantStatus status;
    private final Long restaurantId;
    private final LocalDateTime createdTime;


    public NotificationDetailsDto(Notification notification) {

        this.content = notification.getContent();
        this.status = notification.getStatus();
        this.restaurantId = notification.getRestaurantId();
        this.createdTime = notification.getCreatedTime();
    }
}
