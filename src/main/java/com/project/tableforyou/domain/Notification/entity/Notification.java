package com.project.tableforyou.domain.Notification.entity;

import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(columnDefinition = "boolean default true")
    private boolean isRead = false;

    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    private RestaurantStatus status;

    @Column(name = "created_time")
    @CreatedDate
    private LocalDateTime createdTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notification(String content, Long restaurantId, RestaurantStatus status, User user) {
        this.content = content;
        this.restaurantId = restaurantId;
        this.status = status;
        this.user = user;
    }

    public void setReadStatus(boolean isRead) {
        this.isRead = isRead;
    }
}
