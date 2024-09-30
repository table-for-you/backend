package com.project.tableforyou.common.mail.service;

import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.common.mail.MailType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class FindPassService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /* 임시 비밀번호 메일로 보내기 */
    @Transactional
    public void sendPassToMail(String username, String email) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        String newPass = createPass();
        user.updatePassword(bCryptPasswordEncoder.encode(newPass));

        mailService.sendMail(email, newPass, MailType.PASS);
    }

    /* 임시 비밀번호 생성 */
    private String createPass() {

        try {
            String containsPass = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder sb = new StringBuilder();
            Random random = SecureRandom.getInstanceStrong();

            for (int i = 0; i < 20; i++) {
                sb.append(containsPass.charAt(random.nextInt(containsPass.length())));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate secure random number", e);
        }
    }
}
