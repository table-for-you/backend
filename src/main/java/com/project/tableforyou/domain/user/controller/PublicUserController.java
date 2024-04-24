package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.user.dto.PasswordDto;
import com.project.tableforyou.domain.user.dto.UserRequestDto;
import com.project.tableforyou.domain.user.service.UserService;
import com.project.tableforyou.handler.validate.ValidateHandler;
import com.project.tableforyou.security.auth.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Slf4j
public class PublicUserController {

    private final UserService userService;
    private final ValidateHandler validateHandler;

    /* 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<Object> joinProc(@Valid @RequestBody UserRequestDto dto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = validateHandler.validate(bindingResult);
                log.info("Failed to sign up: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            userService.create(dto);
            return ResponseEntity.ok("회원가입 성공.");
        } catch (Exception e) {
            log.error("Error occurred during sign up: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during sign up");
        }

    }

    /* 아이디 중복 확인 */
    @GetMapping("/check-username")
    public Object checkUsernameExists(@RequestParam("username") String username) {
        return userService.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    @GetMapping("/check-nickname")
    public Object checkNicknameExists(@RequestParam("nickname") String nickname) {
        return userService.existsByNickname(nickname);
    }

    /* 현재 비밀번호 검사 */
    @GetMapping("/check-password")
    public Object checkPassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                @RequestBody PasswordDto passwordDto) {

        return userService.checkPass(principalDetails.getUsername(), passwordDto);
    }
}
