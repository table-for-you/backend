package com.project.tableforyou.auth.controller;

import com.project.tableforyou.auth.dto.LoginDto;
import com.project.tableforyou.auth.service.AuthService;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.TokenException;
import com.project.tableforyou.token.service.RefreshTokenService;
import com.project.tableforyou.utils.cookie.CookieUtil;
import com.project.tableforyou.utils.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.project.tableforyou.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;
import static com.project.tableforyou.utils.jwt.JwtProperties.TOKEN_PREFIX;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {
    
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {

        User user = authService.login(loginDto);

        String role = String.valueOf(user.getRole());

        String accessToken = TOKEN_PREFIX + jwtUtil.generateAccessToken(role, user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(role, user.getUsername());

        refreshTokenService.save(user.getUsername(), refreshToken);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("nickname", user.getNickname());
        responseData.put("accessToken", accessToken);

        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshToken).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(responseData);
    }

    /* accessToken 재발급 */
    @PostMapping("/reissue")
    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenInCookie = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);

        if (refreshTokenInCookie == null) {     // 쿠키에 Refresh Token이 없다면
            throw new TokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String refreshToken = refreshTokenService.findByRefreshToken(refreshTokenInCookie);

        if (jwtUtil.isExpired(refreshToken)) {    // refresh token 만료
            throw new TokenException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String accessTokenReIssue = refreshTokenService.accessTokenReIssue(refreshToken);

        // Refresh token rotation(RTR) 사용
        String refreshTokenReIssue = refreshTokenService.refreshTokenReIssue(refreshToken);

        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshTokenReIssue).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(TOKEN_PREFIX + accessTokenReIssue);
    }

    /* 아이디 찾기 */
    @GetMapping("/find-id")
    public ResponseEntity<String> findingId(@RequestParam("email") @Valid @Email String email) {

        String username = authService.findingId(email);
        return ResponseEntity.ok(username);
    }

    /* 비밀번호 찾기 */
    @PostMapping("/find-pass")
    public ResponseEntity<String> findPass(@RequestParam("email") @Valid @Email String email,
                                           @RequestParam("username") String username) {

        authService.findingPassword(username, email);
        return ResponseEntity.ok("잠시 후 등록하신 메일로 임시 비밀번호가 도착합니다.");
    }
}
