package com.project.tableforyou.common.mail.service;

import com.project.tableforyou.common.mail.MailType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableAsync    // 비동기 처리 활성화.
public class MailService {

    private final JavaMailSender javaMailSender;

    private static final String CODE_SUBJECT = "[TableForYou] 인증번호 메일입니다.";
    private static final String CODE_TEXT = "[TableForYou] \n 인증번호 : ";
    private static final String PASS_SUBJECT = "[TableForYou] 임시 비밀번호입니다.";
    private static final String PASS_TEXT = "[TableForYou] \n 로그인 한 후 회원 정보 수정에서 비밀번호를 변경해 주세요. \n" +
            "임시 비밀번호 : ";
    private static final String MAIL_SENDER = "Table-For-You";

    /* apprication.yml에 지정한 값 들고오기. */
    @Value("${spring.mail.username}")
    private String emailUsername;


    /* 메일 보내기.  */
    @Async  // 비동기 처리.
    public void sendMail(String email, String content, MailType type) {

        MimeMessage message = null;

        try {
            switch (type) {
                case CODE -> message = getCodeMessage(email, content);
                case PASS -> message = getPassMessage(email, content);
            }
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }

    /* Code Message 만들기. */
    private MimeMessage getCodeMessage(String email, String code) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject(CODE_SUBJECT);
        helper.setText(CODE_TEXT + code, true);
        helper.setFrom(new InternetAddress(emailUsername, MAIL_SENDER, "UTF-8"));

        return message;
    }

    /* Pass Message 만들기. */
    private MimeMessage getPassMessage(String email, String pass) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject(PASS_SUBJECT);
        helper.setText(PASS_TEXT + pass, true);
        helper.setFrom(new InternetAddress(emailUsername, MAIL_SENDER, "UTF-8"));

        return message;
    }
}
