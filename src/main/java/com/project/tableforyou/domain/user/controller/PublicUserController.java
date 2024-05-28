package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.user.dto.SignUpDto;
import com.project.tableforyou.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/users")
@RequiredArgsConstructor
@Slf4j
public class PublicUserController {

    private final UserService userService;

    /* 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody SignUpDto signUpDto) {

        userService.signUp(signUpDto);
        return ResponseEntity.ok("회원가입 성공.");
    }

    /* 아이디 중복 확인 */
    @GetMapping("/check-username")
    public boolean checkUsernameExists(@RequestParam("username") String username) {
        return userService.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    @GetMapping("/check-nickname")
    public boolean checkNicknameExists(@RequestParam("nickname") String nickname) {
        return userService.existsByNickname(nickname);
    }
}
