package com.project.tableforyou.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCodeService {


    private final MailService mailService;
    private final Map<String, VerificationData> codeMap = new ConcurrentHashMap<>();  // 메일 인증번호 확인용. 멀티스레드 측면에서 안전성 이점.
    private static final int EMAIL_VERIFICATION_EXPIRATION_MINUTES = 3; // 이메일 인증 유효 시간 3분
    private static final int EMAIL_RESEND_VALIDITY_MINUTES = 1; // 이메일 재전송 유효 시간 1분

    /* 회원가입 이메일 인증 번호. */
    public boolean sendCodeToMail(String email) {
        VerificationData storedData = codeMap.get(email);
        if (storedData != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime expirationTime = storedData.getTimestamp().plusMinutes(EMAIL_RESEND_VALIDITY_MINUTES); // 이전에 보낸 메일로부터 1분이 지났는지 확인

            if (currentTime.isBefore(expirationTime)) {
                // 이전에 보낸 메일로부터 1분이 지나지 않았으면 메일을 보낼 수 없음
                log.warn("Cannot send code to email {} as a recent code was sent less than a minute ago", email);
                return false;
            } else {
                // 이전에 보낸 메일로부터 1분이 지났으면 해당 데이터 삭제
                codeMap.remove(email);
            }
        }
        String authCode = createCode();
        mailService.sendMail(email, authCode);
        codeMap.put(email, new VerificationData(authCode, LocalDateTime.now()));
        return true;
    }

    /* 인증번호 만드는 메서드. */
    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();   // 암호학적으로 안전한 무작위 수를 생성. 인증번호는 보안적으로 중요하기 SecureRandom 사용.
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 6; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.info("Failed to create secure random instance", e);
            throw new RuntimeException("Failed to generate secure random number", e);
        }
    }

    /* 인증번호 확인 메서드. */
    public boolean verifiedCode(String email, String code) {
        VerificationData storedCode = codeMap.get(email);
        if(storedCode != null && storedCode.getCode().equals(code)) {
            LocalDateTime expirationTime = storedCode.getTimestamp().plusMinutes(EMAIL_VERIFICATION_EXPIRATION_MINUTES);

            if (LocalDateTime.now().isBefore(expirationTime)) {     // 유효 시간이 지나지 않았다면
                codeMap.remove(email);      // 인증코드가 맞다면 인증코드 삭제.
                log.info("Authentication code verified successfully for email: {}", email);
                return true;
            } else {
                codeMap.remove(email); // 유효 기간이 지났으면 해당 데이터 삭제
                log.warn("Authentication code has expired for email: {}", email);
            }
        }
        return false;
    }
}