package com.project.tableforyou.auth.controller;

import com.project.tableforyou.auth.dto.LoginDto;
import com.project.tableforyou.auth.service.AuthService;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.TokenException;
import com.project.tableforyou.utils.cookie.CookieUtil;
import com.project.tableforyou.utils.jwt.JwtUtil;
import com.project.tableforyou.token.dto.RefreshTokenDto;
import com.project.tableforyou.token.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.project.tableforyou.utils.jwt.JwtProperties.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {
    
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response, BindingResult bindingResult) throws IOException {

        User user = authService.login(loginDto);

        String role = String.valueOf(user.getRole());

        String accessToken = TOKEN_PREFIX + jwtUtil.generateAccessToken(role, user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(role, user.getUsername());

        saveRefreshToken(user.getUsername(), refreshToken);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("nickname", user.getNickname());
        responseData.put("accessToken", accessToken);

        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshToken).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenInCookie = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);

        if (refreshTokenInCookie == null) {     // 쿠키에 Refresh Token이 없다면
            throw new TokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        RefreshTokenDto refreshToken = refreshTokenService.findByRefreshToken(refreshTokenInCookie);

        if (jwtUtil.isExpired(refreshToken.getRefreshToken())) {    // refresh token 만료
            throw new TokenException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String accessTokenReIssue = refreshTokenService.accessTokenReIssue(refreshToken.getRefreshToken());

        // Refresh token rotation(RTR) 사용
        String refreshTokenReIssue = refreshTokenService.refreshTokenReIssue(refreshToken, refreshToken.getRefreshToken());

        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshTokenReIssue).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok(TOKEN_PREFIX + accessTokenReIssue);
    }

    /* redis에 refreshToken 저장 */
    private void saveRefreshToken(String username, String refreshToken) {
        RefreshTokenDto saveRefreshToken = RefreshTokenDto.builder()
                .username(username)
                .refreshToken(refreshToken)
                .build();
        refreshTokenService.save(saveRefreshToken);
    }
}
