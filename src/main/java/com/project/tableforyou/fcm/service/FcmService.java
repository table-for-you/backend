package com.project.tableforyou.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {

    public void sendNotification(String targetToken, String title, String content) {

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().sendAsync(message);
        } catch (Exception e) {
            log.error("An error occurred while processing Notification for targetToken: {}", targetToken);
        }
    }
}
