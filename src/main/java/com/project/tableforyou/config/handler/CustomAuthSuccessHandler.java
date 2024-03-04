package com.project.tableforyou.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.config.auth.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();   // 닉네임을 받아오기 위해.
        String nickname = principalDetails.getUser().getNickname();
        System.out.println("=========" + request.getSession().getId());
        // 세션 ID를 쿠키로 설정
        Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
        cookie.setPath("/");
        response.addCookie(cookie);

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("nickname", nickname);

        String userInfoJson = objectMapper.writeValueAsString(userInfo);        // JSON으로 변환.

        // HTTP 응답에 데이터 전송
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(userInfoJson);
    }
}
