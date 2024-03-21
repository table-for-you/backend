package com.project.tableforyou.handler.logoutHandler;

import com.project.tableforyou.handler.exceptionHandler.CustomException;
import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import com.project.tableforyou.redis.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshToken = getRefreshToken(request);

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESHTOKEN_NOT_FOUND);
        }

        refreshTokenService.delete(refreshToken);       // 로그아웃 시 redis에서 refreshToken 삭제
    }

    private String getRefreshToken(HttpServletRequest request) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("RefreshToken")) {

                refreshToken = cookie.getValue();
            }
        }
        return refreshToken;
    }
}