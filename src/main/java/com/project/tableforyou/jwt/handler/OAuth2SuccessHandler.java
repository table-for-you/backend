package com.project.tableforyou.jwt.handler;

import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.jwt.JwtUtil;
import com.project.tableforyou.redis.domain.RefreshTokenDto;
import com.project.tableforyou.redis.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import static com.project.tableforyou.jwt.JwtProperties.*;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = principalDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        String role = iterator.next().getAuthority();
        role = role.split("_")[1];      // ROLE_ 접두사 빼기 위해.

        String username = principalDetails.getUsername();

        String accessToken = jwtUtil.generateAccessToken(role, username);     // Access Token 발급
        String refreshToken = jwtUtil.generateRefreshToken(role, username);   // Refresh Token 발급

        saveRefreshToken(username, refreshToken);

        response.addHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessToken);    // 헤더에 access Token 추가
        response.addHeader("Set-Cookie", createCookie(REFRESH_COOKIE_VALUE, refreshToken).toString());         // 쿠키에 refresh Token 값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

    }

    /* redis에 refreshToken 저장  */
    private void saveRefreshToken(String username, String refreshToken) {
        RefreshTokenDto saveRefreshToken = RefreshTokenDto.builder()
                .username(username)
                .refreshToken(refreshToken)
                .build();
        refreshTokenService.save(saveRefreshToken);
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