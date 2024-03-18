package com.project.tableforyou.jwt.handler;

import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.domain.dto.AuthDto;
import com.project.tableforyou.jwt.JwtUtil;
import com.project.tableforyou.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.project.tableforyou.jwt.JwtProperties.*;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(principalDetails);     // Access Token 발급
        String refreshToken = jwtUtil.generateRefreshToken(principalDetails);   // Refresh Token 발급

        String username = principalDetails.getUsername();

        if(authService.existsByUsername(username)) {
            authService.delete(username);
        }

        String refreshUUID = getRefreshUUID(refreshToken, username);

        response.addHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessToken);    // 헤더에 access Token 추가
        response.addHeader("Set-Cookie", createCookie(REFRESH_COOKIE_VALUE, refreshUUID).toString());         // 쿠키에 refresh Token index 값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

    }
    /* Refresh Token db저장 및 key값 가져오기 */
    private String getRefreshUUID(String refreshToken, String username) {
        AuthDto authDTO = AuthDto.builder()
                .token(refreshToken)
                .username(username)
                .build();

        return authService.save(authDTO);
    }

    /* 쿠키 생성 메서드 */
    private ResponseCookie createCookie(String key, String value) {

        ResponseCookie cookie = ResponseCookie.from(key, value)
                .path("/")
                .httpOnly(true)
                .maxAge(24*60*60)
                .secure(true)
                .sameSite("None")
                .build();

        return cookie;
    }
}