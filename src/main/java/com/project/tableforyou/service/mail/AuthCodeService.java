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
    private static final int VERIFICATION_EXPIRATION_MINUTES = 3; // 이메일 인증 유효 시간 3분
    private static final int RESEND_VALIDITY_MINUTES = 1; // 이메일 재전송 유효 시간 1분
    private static final String SEND_EMAIL = "mail";
    private static final String SEND_PHONE = "phone";

    /* 회원가입 이메일 인증 번호. */
    public boolean sendCodeToMail(String email) {
        return sendCode(email, SEND_EMAIL);
    }

    /* 회원가입 핸드폰 인증번호 확인 메서드. (임시로 만든 메서드) */
    public boolean sendCodeToPhone(String phone) {
        return sendCode(phone, SEND_PHONE);
    }

    private boolean sendCode(String key, String type) {
        VerificationData storedData = codeMap.get(key);
        if (storedData != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime expirationTime = storedData.getTimestamp().plusMinutes(RESEND_VALIDITY_MINUTES); // 이전에 보낸 메일로부터 1분이 지났는지 확인

            if (currentTime.isBefore(expirationTime)) {
                // 이전에 보낸 메일로부터 1분이 지나지 않았으면 메일을 보낼 수 없음
                log.warn("A recent code was sent less than a minute ago, so unable to send the code. {}", key);
                return false;
            } else {
                // 이전에 보낸 메일로부터 1분이 지났으면 해당 데이터 삭제
                codeMap.remove(key);
            }
        }
        String authCode = createCode();
        switch (type) {
            case "phone" -> log.info("[tableForYou] 본인인증번호 : {}", authCode);
            case "mail" -> mailService.sendMail(key, authCode);
        }
        codeMap.put(key, new VerificationData(authCode, LocalDateTime.now()));
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
    public boolean verifiedCode(String key, String code) {
        VerificationData storedCode = codeMap.get(key);
        if(storedCode != null && storedCode.getCode().equals(code)) {
            LocalDateTime expirationTime = storedCode.getTimestamp().plusMinutes(VERIFICATION_EXPIRATION_MINUTES);

            if (LocalDateTime.now().isBefore(expirationTime)) {     // 유효 시간이 지나지 않았다면
                codeMap.remove(key);      // 인증코드가 맞다면 인증코드 삭제.
                log.info("Authentication code verified successfully: {}", key);
                return true;
            } else {
                codeMap.remove(key); // 유효 기간이 지났으면 해당 데이터 삭제
                log.warn("Authentication code has expired: {}", key);
            }
        }
        return false;
    }
}