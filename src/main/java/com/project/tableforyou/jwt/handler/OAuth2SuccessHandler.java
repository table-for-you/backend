package com.project.tableforyou.jwt.handler;

import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.token.service.RefreshTokenService;
import com.project.tableforyou.utils.cookie.CookieUtil;
import com.project.tableforyou.utils.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import static com.project.tableforyou.utils.jwt.JwtProperties.*;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
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

        refreshTokenService.save(username, refreshToken);

        response.addHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessToken);    // 헤더에 access Token 추가
        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshToken).toString());         // 쿠키에 refresh Token 값 저장.
        response.setStatus(HttpServletResponse.SC_OK);

    }
}