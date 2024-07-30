package com.project.tableforyou.mail.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[메일 API]", description = "메일 관련 API")
public interface MailApi {

    @Operation(summary = "이메일 인증 번호 보내기", description = "이메일 인증 번호 보내기위한 API입니다.")
    ResponseEntity<String> sendCodeToMail(@RequestParam("email") @Valid @Email String email);

    @Operation(summary = "인증 번호 확인하기", description = "인증 번호 확인하기위한 API입니다.")
    ResponseEntity<?> verifyCode(@RequestParam(value = "email") @Valid @Email String email,
                                 @RequestParam("code") String code);
}
