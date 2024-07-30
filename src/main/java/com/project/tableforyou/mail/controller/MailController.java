package com.project.tableforyou.mail.controller;

import com.project.tableforyou.mail.api.MailApi;
import com.project.tableforyou.mail.service.CodeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
@Slf4j
public class MailController implements MailApi {

    private final CodeService codeService;

    /* 이메일 인증 번호 보내기 */
    @Override
    @PostMapping("/emails/verification-request")
    public ResponseEntity<String> sendCodeToMail(@RequestParam("email") @Valid @Email String email) {

        codeService.sendCodeToMail(email);
        return ResponseEntity.ok("인증메일 보내기 성공.");
    }

    /* 인증 번호 확인 */
    @Override
    @PostMapping("/code-verification")
    public ResponseEntity<?> verifyCode(@RequestParam(value = "email") @Valid @Email String email,
                             @RequestParam("code") String code) {

        return ResponseEntity.ok(codeService.verifiedCode(email, code));
    }
}
