package com.project.tableforyou.mail.controller;

import com.project.tableforyou.domain.user.service.UserService;
import com.project.tableforyou.mail.service.CodeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final CodeService codeService;
    private final UserService userService;

    /* 이메일 인증 번호 보내기 */
    @PostMapping("/emails/verification-request")
    public ResponseEntity<String> sendCodeToMail(@RequestParam("email") @Valid @Email String email) {

        try {
            if(email != null && !email.equals("")) {
                boolean codeSent = codeService.sendCodeToMail(email);
                if (codeSent) {
                    return ResponseEntity.ok("인증메일 보내기 성공.");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증메일 보내기 실패. 1분 후 재전송.");
                }
            } else {
                log.error("Invalid email address received from client: {}", email);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일을 제대로 입력하세요.");
            }
        } catch (Exception e) {
            log.error("Failed to send verification email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증메일 보내기 실패.");
        }
    }

    /* 인증 번호 확인 */
    @PostMapping("/code-verification")
    public Object verifyCode(@RequestParam(value = "email") @Valid @Email String email,
                             @RequestParam("code") String code) {

        if (codeService.verifiedCode(email, code)) {
            if (userService.existsByEmail(email)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입된 이메일입니다.");
            }
            return true;
        } else
            return false;

    }
}
