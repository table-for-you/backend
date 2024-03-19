package com.project.tableforyou.controller;

import com.project.tableforyou.jwt.JwtUtil;
import com.project.tableforyou.redis.domain.RefreshTokenDto;
import com.project.tableforyou.redis.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.tableforyou.jwt.JwtProperties.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenInCookie = getRefreshToken(request);

        if (refreshTokenInCookie == null) {     // 쿠키에 Refresh Token이 없다면
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refresh token null");
        }

        RefreshTokenDto refreshToken = refreshTokenService.findByRefreshToken(refreshTokenInCookie);

        if (jwtUtil.isExpired(refreshToken.getRefreshToken())) {    // refresh token 만료
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("refresh expired");
        }

        String username = jwtUtil.getUsername(refreshToken.getRefreshToken());
        String role = jwtUtil.getRole(refreshToken.getRefreshToken());

        String accessTokenReIssue = jwtUtil.generateAccessToken(role, username);      // 재발급

        refreshTokenReIssue(response, refreshToken, username, role);        // Refresh token rotation(RTR) 사용

        response.setHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessTokenReIssue);

        return ResponseEntity.ok("Access Token reIssue");
    }

    /* Refresh token rotation(RTR) 사용 */
    private void refreshTokenReIssue(HttpServletResponse response, RefreshTokenDto refreshToken, String username, String role) {
        refreshTokenService.delete(refreshToken.getRefreshToken());

        String refreshTokenReIssue = jwtUtil.generateRefreshToken(role, username);
        RefreshTokenDto refreshTokenReIssueDto = RefreshTokenDto.builder()
                .username(username)
                .refreshToken(refreshTokenReIssue)
                .build();

        refreshTokenService.save(refreshTokenReIssueDto);
        response.addHeader("Set-Cookie", createCookie(REFRESH_COOKIE_VALUE, refreshTokenReIssue).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /* 쿠키 값에서 Refresh Token 가져오기 */
    private String getRefreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        String refreshToken = null;
        for(Cookie cookie: cookies) {
            if(cookie.getName().equals(REFRESH_COOKIE_VALUE))
                refreshToken = cookie.getValue();
        }
        return refreshToken;
    }

    /* 쿠키 생성 메서드 */
    private ResponseCookie createCookie(String key, String value) {

        return ResponseCookie.from(key, value)
                .path("/")
                .httpOnly(true)
                .maxAge(24*60*60)
                .secure(true)
                .sameSite("None")
                .build();
    }
}
