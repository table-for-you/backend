package com.project.tableforyou.domain.user.controller;

import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.RefreshTokenException;
import com.project.tableforyou.utils.cookie.CookieUtil;
import com.project.tableforyou.utils.jwt.JwtUtil;
import com.project.tableforyou.refreshToken.dto.RefreshTokenDto;
import com.project.tableforyou.refreshToken.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.tableforyou.utils.jwt.JwtProperties.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenInCookie = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);

        if (refreshTokenInCookie == null) {     // 쿠키에 Refresh Token이 없다면
            throw new RefreshTokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        RefreshTokenDto refreshToken = refreshTokenService.findByRefreshToken(refreshTokenInCookie);

        if (jwtUtil.isExpired(refreshToken.getRefreshToken())) {    // refresh token 만료
            throw new RefreshTokenException(ErrorCode.REFRESG_TOKEN_EXPIRED);
        }


        String accessTokenReIssue = refreshTokenService.accessTokenReIssue(refreshToken.getRefreshToken());

        // Refresh token rotation(RTR) 사용
        String refreshTokenReIssue = refreshTokenService.refreshTokenReIssue(refreshToken, refreshToken.getRefreshToken());

        response.setHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessTokenReIssue);
        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshTokenReIssue).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok("Access Token reIssue");
    }
}
