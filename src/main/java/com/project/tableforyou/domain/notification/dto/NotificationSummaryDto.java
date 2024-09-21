package com.project.tableforyou.domain.notification.dto;

import com.project.tableforyou.domain.notification.entity.Notification;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationSummaryDto {

    private final Long id;
    private final String content;
    private final RestaurantStatus status;
    private final boolean isRead;
    private final LocalDateTime createdTime;

    public NotificationSummaryDto(Notification notification) {
        this.id = notification.getId();
        this.content = notification.getContent();
        this.status = notification.getStatus();
        this.isRead = notification.isRead();
        this.createdTime = notification.getCreatedTime();
    }
}
