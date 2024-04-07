package com.project.tableforyou.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.domain.user.dto.LoginDto;
import com.project.tableforyou.handler.authFailureHandler.CustomAuthFailureHandler;
import com.project.tableforyou.utils.cookie.CookieUtil;
import com.project.tableforyou.utils.jwt.JwtUtil;
import com.project.tableforyou.refreshToken.dto.RefreshTokenDto;
import com.project.tableforyou.refreshToken.service.RefreshTokenService;
import com.project.tableforyou.security.auth.PrincipalDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.project.tableforyou.utils.jwt.JwtProperties.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        LoginDto loginDto;
        try {                                                               // JSON으로 로그인 값을 받기 위해.
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginDto = objectMapper.readValue(messageBody, LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = principalDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        String role = iterator.next().getAuthority();
        role = role.split("_")[1];      // ROLE_ 접두사 빼기 위해.

        String username = principalDetails.getUsername();

        String accessToken = jwtUtil.generateAccessToken(role, username);     // access Token 발급
        String refreshToken = jwtUtil.generateRefreshToken(role, username);   // refresh Token 발급


        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("nickname", principalDetails.getUser().getNickname());     // 프론트에 nickname 보내기

        String userInfoJson = objectMapper.writeValueAsString(userInfo);        // json 형태로 변환.

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(userInfoJson);

        saveRefreshToken(username, refreshToken);               // refreshToken redis에 저장.

        response.setHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessToken);    // 헤더에 access Token 저장.
        response.addHeader("Set-Cookie", cookieUtil.createCookie(REFRESH_COOKIE_VALUE, refreshToken).toString());         // 쿠키에 refresh Token값 저장.
        response.setStatus(HttpServletResponse.SC_OK);      // 상태 코드 200
    }

    /* redis에 refreshToken 저장 */
    private void saveRefreshToken(String username, String refreshToken) {
        RefreshTokenDto saveRefreshToken = RefreshTokenDto.builder()
                .username(username)
                .refreshToken(refreshToken)
                .build();
        refreshTokenService.save(saveRefreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        customAuthFailureHandler.onAuthenticationFailure(request, response, failed);
    }
}

