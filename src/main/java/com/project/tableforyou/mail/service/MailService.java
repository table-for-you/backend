package com.project.tableforyou.mail.service;

import com.project.tableforyou.mail.MailType;
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
    private static final String CODE_SUBJECT = "[TableForYou] 인증번호 메일입니다.";
    private static final String CODE_TEXT = "[TableForYou] \n 인증번호 : ";
    private static final String PASS_SUBJECT = "[TableForYou] 임시 비밀번호입니다.";
    private static final String PASS_TEXT = "[TableForYou] \n 로그인 한 후 회원 정보 수정에서 비밀번호를 변경해 주세요. \n" +
            "임시 비밀번호 : ";

    /* apprication.yml에 지정한 값 들고오기. */
    @Value("${spring.mail.username}")
    private String emailUsername;


    /* 메일 보내기.  */
    @Async  // 비동기 처리.
    public void sendMail(String email, String content, MailType type) {

        SimpleMailMessage message = null;

        switch (type) {
            case CODE -> message = getCodeMessage(email, content);
            case PASS -> message = getPassMessage(email, content);
        }

        try {
            javaMailSender.send(message);
            log.info("Successfully sent verification email to {}", email);
        } catch (MailException e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }

    /* Code Message 만들기. */
    private SimpleMailMessage getCodeMessage(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(CODE_SUBJECT);
        message.setText(CODE_TEXT + code);
        message.setFrom(emailUsername);

        return message;
    }

    /* Pass Message 만들기. */
    private SimpleMailMessage getPassMessage(String email, String pass) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(PASS_SUBJECT);
        message.setText(PASS_TEXT + pass);
        message.setFrom(emailUsername);

        return message;
    }
}
