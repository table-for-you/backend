package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.domain.user.dto.UserDto;
import com.project.tableforyou.domain.user.service.UserService;
import com.project.tableforyou.mail.service.CodeService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final CodeService CodeService;

    /* 회원가입 과정 */
    @PostMapping("/joinProc")
    public ResponseEntity<Object> joinProc(@Valid @RequestBody UserDto.Request dto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = userService.validateHandler(bindingResult);
                log.info("Failed to sign up: {}", errors);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
            }
            userService.create(dto);
            return ResponseEntity.ok("회원가입 성공.");
        } catch (Exception e) {
            log.error("Error occurred during sign up: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during sign up");
        }

    }

    /* 회원 불러오기 */
    @GetMapping("/{username}")
    public UserDto.Response read(@PathVariable(name = "username") String username) {
        return userService.findByUsername(username);
    }

    /* 회원 업데이트 */
    @PutMapping("/update")
    public ResponseEntity<String> update(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @RequestBody UserDto.UpdateRequest dto) {

        userService.update(principalDetails.getUsername(), dto);
        return ResponseEntity.ok("회원 업데이트 성공.");

    }

    /* 회원 삭제 */
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        userService.delete(principalDetails.getUsername());
        return ResponseEntity.ok("회원 삭제 성공.");
    }

    /* 이메일 인증 번호 보내기 */
    @PostMapping("/emails/verification-request")
    public ResponseEntity<String> sendCodeToMail(@RequestParam("email") @Valid @Email String email) {

        try {
            if(email != null && !email.equals("")) {
                boolean codeSent = CodeService.sendCodeToMail(email);
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

    /* 핸드폰 인증 번호 보내기 ( 확인용으로 log에 찍히게 함. ) */
    @PostMapping("/phone/verification-request")
    public ResponseEntity<String> sendCodeToPhone(@RequestParam("phone") String phone) {

        try {
            if(phone != null && !phone.equals("")) {
                boolean codeSent = CodeService.sendCodeToPhone(phone);
                if (codeSent) {
                    return ResponseEntity.ok("인증번호 보내기 성공.");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증번호 보내기 실패. 1분 후 재전송.");
                }
            }
            else {
                log.error("Invalid phone number received from client: {}", phone);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("휴대전화를 제대로 입력하세요.");
            }
        } catch (Exception e) {
            log.error("Failed to send verification phone: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증번호 보내기 실패.");
        }
    }

    /* 인증 번호 확인 */
    @PostMapping("/code-verification")
    public Object verifyCode(@RequestParam(value = "email", required = false) @Valid @Email String email,
                              @RequestParam(value = "phone", required = false) String phone,
                              @RequestParam("code") String code) {
        if(email != null) {
            if (CodeService.verifiedCode(email, code)) {
                if (userService.existsByEmail(email)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입된 이메일입니다.");
                }
                return true;
            }
            else
                return false;
        }
        else {
            if (CodeService.verifiedCode(phone, code))
                return true;
            else
                return false;
        }
    }

    /* 아이디 중복 확인 */
    @GetMapping("/checkUsername")
    public Object checkUsernameExists(@RequestParam("username") String username) {
        return userService.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    @GetMapping("/checkNickname")
    public Object checkNicknameExists(@RequestParam("nickname") String nickname) {
        return userService.existsByNickname(nickname);
    }

    @GetMapping("/session")            // 세션 확인용
    public String sessionInfo(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return "세션이 없습니다";
        }
        // session.setMaxInactiveInterval(3605);

        log.info("sessionId = {}", session.getId());
        log.info("getMaxInactiveInterval={}", session.getMaxInactiveInterval());
        // *참고 :application.yml에서 설정 가능한 최소 시간은 1분이며, 분단위로 설정해야 합니다.
        log.info("creationTime={}", new Date(session.getCreationTime()));
        log.info("lastAccessTime={}", new Date(session.getLastAccessedTime()));

        return "세션출력";

    }
}