package com.project.tableforyou.domain.Notification.service;

import com.project.tableforyou.domain.Notification.dto.NotificationDetailsDto;
import com.project.tableforyou.domain.Notification.dto.NotificationSummaryDto;
import com.project.tableforyou.domain.Notification.entity.Notification;
import com.project.tableforyou.domain.Notification.repository.NotificationRepository;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private static final String RESTAURANT_APPROVED_CONTENT = "가게가 승인되었습니다!";
    private static final String RESTAURANT_REJECT_CONTENT = "가게가 승인 거절되었습니다.";

    /* 알림 생성 */
    @Transactional
    public void createNotification(RestaurantStatus status, Long restaurantId, User owner) {

        String content = "";
        if (status == RestaurantStatus.APPROVED)
            content = RESTAURANT_APPROVED_CONTENT;

        if (status == RestaurantStatus.REJECT)
            content = RESTAURANT_REJECT_CONTENT;

        notificationRepository.save(
                Notification.builder()
                        .content(content)
                        .restaurantId(restaurantId)
                        .status(status)
                        .user(owner)
                        .build());
    }

    /* 알림 목록 들고오기 */
    @Transactional(readOnly = true)
    public Page<NotificationSummaryDto> readAllNotification(Long userId, Pageable pageable) {

        Page<Notification> notifications = notificationRepository.findByUser_Id(userId, pageable);
        return notifications.map(NotificationSummaryDto::new);
    }

    /* 특정 알림 불러오기 */
    @Transactional
    public NotificationDetailsDto readNotification(Long userId, Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!userId.equals(notification.getUser().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        notification.setReadStatus(true);

        return new NotificationDetailsDto(notification);
    }
}
