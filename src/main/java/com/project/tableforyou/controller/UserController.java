package com.project.tableforyou.controller;

import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.domain.dto.UserDto;
import com.project.tableforyou.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    /* 회원가입 과정 */
    @PostMapping("/joinProc")
    public ResponseEntity<String> joinProc(@Valid @RequestBody UserDto.Request dto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> validated = userService.validateHandler(bindingResult);
                log.info("Failed to sign up: {}", validated);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패" + validated);
            }
            userService.create(dto);
            return ResponseEntity.ok("회원가입 성공.");
        } catch (Exception e) {
            log.error("Error occurred during sign up: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during sign up");
        }

    }

    /* 회원 불러오기 */
    @GetMapping("/{user_id}")
    public UserDto.Response read(@PathVariable Long user_id) {
        return userService.findById(user_id);
    }

    /* 회원 전체 불러오기, 페이징 처리 */
    @GetMapping
    public Page<UserDto.Response> readAll(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.userPageList(pageable);
    }

    /* 회원 업데이트 */
    @PutMapping("/update")
    public ResponseEntity<String> update(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @RequestBody UserDto.Request dto) {
        try {
            userService.update(principalDetails.getUser().getId(), dto);
            return ResponseEntity.ok("회원 업데이트 성공.");
        } catch (Exception e) {
            log.error("Error occurred during member update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 업데이트 실패");
        }
    }

    /* 회원 삭제 */
    @DeleteMapping("/{user_id}")
    public ResponseEntity<String> delete(@PathVariable Long user_id) {

        try {
            userService.delete(user_id);
            return ResponseEntity.ok("회원 삭제 성공.");
        } catch (Exception e) {
            log.error("Error occurred during member deletion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 업데이트 실패");
        }
    }

    /* 이메일 인증 번호 보내기 */
    @PostMapping("/emails/verification-request")
    public ResponseEntity<String> sendCode(@RequestParam("email") @Valid @Email String email) {

        try {
            userService.sendCodeToMail(email);
            return ResponseEntity.ok("인증메일 보내기 성공.");
        } catch (Exception e) {
            log.error("Failed to send verification email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증메일 보내기 실패.");
        }
    }

    /* 인증 번호 확인 */
    @GetMapping("/emails/verification")
    public String verifyCode(@RequestParam("email") @Valid @Email String email,
                             @RequestParam("code") String code) {

        if(userService.verifiedCode(email, code))
            return "인증 성공";
        else
            return "인증 실패";
    }

    /* 아이디 중복 확인 */
    @GetMapping("/checkUsername")
    public boolean checkUsernameExists(@RequestParam("username") String username) {
        return userService.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    @GetMapping("/checkNickname")
    public boolean checkNicknameExists(@RequestParam("nickname") String nickname) {
        return userService.existsByNickname(nickname);
    }
}
