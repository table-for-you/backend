package com.project.tableforyou.domain.notification.controller;

import com.project.tableforyou.common.utils.api.ApiUtil;
import com.project.tableforyou.domain.notification.api.NotificationApi;
import com.project.tableforyou.domain.notification.service.NotificationService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @Override
    @GetMapping
    public ResponseEntity<?> readAllNotification(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(notificationService.readAllNotification(principalDetails.getId(), pageable));
    }

    @Override
    @GetMapping("/{notificationId}")
    public ResponseEntity<?> readNotification(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @PathVariable Long notificationId) {

        return ResponseEntity.ok(notificationService.readNotification(principalDetails.getId(), notificationId));
    }

    @GetMapping("/size")
    public ResponseEntity<?> getNotificationSize(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(ApiUtil.from(
                notificationService.getUnReadNotificationSize(principalDetails.getId()))
        );
    }

}
