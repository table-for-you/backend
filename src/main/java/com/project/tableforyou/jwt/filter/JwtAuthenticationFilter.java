package com.project.tableforyou.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.handler.authFailureHandler.CustomAuthFailureHandler;
import com.project.tableforyou.domain.dto.AuthDto;
import com.project.tableforyou.domain.dto.LoginDto;
import com.project.tableforyou.jwt.JwtUtil;
import com.project.tableforyou.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.project.tableforyou.jwt.JwtProperties.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final AuthService authService;
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

        String accessToken = jwtUtil.generateAccessToken(principalDetails);     // access Token 발급
        String refreshToken = jwtUtil.generateRefreshToken(principalDetails);   // refresh Token 발급

        String username = principalDetails.getUsername();

        if (authService.existsByUsername(username)) {
            authService.delete(username);
        }

        String refreshUUID = getRefreshUUID(refreshToken, username);

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("nickname", principalDetails.getUser().getNickname());     // 프론트에 nickname 보내기

        String userInfoJson = objectMapper.writeValueAsString(userInfo);        // json 형태로 변환.

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(userInfoJson);

        response.setHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessToken);    // 헤더에 access Token 저장.
        response.addCookie(createCookie(REFRESH_COOKIE_VALUE, refreshUUID));         // 쿠키에 refresh Token index 값 저장.
        response.setStatus(HttpServletResponse.SC_OK);      // 상태 코드 200
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
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(false);
        //cookie.setPath("/");
        cookie.setHttpOnly(false);

        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        customAuthFailureHandler.onAuthenticationFailure(request, response, failed);
    }
}

