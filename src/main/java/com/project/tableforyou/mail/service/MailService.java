package com.project.tableforyou.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
@EnableAsync    // 비동기 처리 활성화.
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String subject = "[TableForYou] 인증번호 메일입니다.";
    private static final String text = "[TableForYou] 인증번호 : ";

    /* apprication.yml에 지정한 값 들고오기. */
    @Value("${spring.mail.username}")
    private String emailUsername;


    /* 메일 보내기.  */
    @Async  // 비동기 처리.
    public void sendMail(String email, String code) {

        SimpleMailMessage message = getMessage(email, code);
        try {
            javaMailSender.send(message);
            log.info("Successfully sent verification email to {}", email);
        } catch (MailException e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }

    /* message 만들기. */
    private SimpleMailMessage getMessage(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text + code);
        message.setFrom(emailUsername);

        return message;
    }
}
