package com.project.tableforyou.domain.notification.service;

import com.project.tableforyou.common.fcm.service.FcmService;
import com.project.tableforyou.common.fcm.util.FcmProperties;
import com.project.tableforyou.domain.notification.dto.NotificationDetailsDto;
import com.project.tableforyou.domain.notification.dto.NotificationSummaryDto;
import com.project.tableforyou.domain.notification.entity.Notification;
import com.project.tableforyou.domain.notification.repository.NotificationRepository;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    /* 알림 생성 */
    @Transactional
    public void createRestaurantStatusNotification(String fcmToken, RestaurantStatus status, Long restaurantId, User owner) {

        String content = "";
        if (status == RestaurantStatus.APPROVED) {
            content = FcmProperties.RESTAURANT_APPROVED_TITLE;
            /*fcmService.sendNotification(
                    fcmToken,
                    FcmProperties.RESTAURANT_APPROVED_TITLE,
                    FcmProperties.RESTAURANT_APPROVED_CONTENT
            );*/
        }

        if (status == RestaurantStatus.REJECT) {
            content = FcmProperties.RESTAURANT_REJECT_TITLE;
            /*fcmService.sendNotification(
                    fcmToken,
                    FcmProperties.RESTAURANT_REJECT_TITLE,
                    FcmProperties.RESTAURANT_REJECT_CONTENT
            );*/
        }

        notificationRepository.save(
                Notification.builder()
                        .content(content)
                        .restaurantId(restaurantId)
                        .status(status)
                        .user(owner)
                        .build());
    }

    /* 알림 생성 및 FCM 전송 */
    @Transactional
    public void createReservationNotification(String fcmToken, String title, String content, Long restaurantId, User user) {

        // FCM 알림 전송
        //fcmService.sendNotification(fcmToken, title, content);

        // 알림 저장
        notificationRepository.save(
                Notification.builder()
                        .content(content)
                        .restaurantId(restaurantId)
                        .user(user)
                        .build()
        );
    }

    /* 알림 목록 들고오기 */
    @Transactional(readOnly = true)
    public List<NotificationSummaryDto> readAllNotification(Long userId) {

        List<Notification> notifications = notificationRepository.findByUser_Id(userId);
        return notifications.stream().map(NotificationSummaryDto::new).collect(Collectors.toList());
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

    @Transactional(readOnly = true)
    public Long getUnReadNotificationSize(Long userid) {

        return notificationRepository.countUnreadNotificationsByUserId(userid);
    }
}
