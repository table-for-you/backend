package com.project.tableforyou.config.handler;

import com.project.tableforyou.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final AuthService authService;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        UUID refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = UUID.fromString(cookie.getValue());
            }
        }

        authService.revokedRefreshByLogout(refresh);
    }
}