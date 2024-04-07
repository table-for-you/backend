package com.project.tableforyou.handler.logoutHandler;

import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.RefreshTokenException;
import com.project.tableforyou.refreshToken.service.RefreshTokenService;
import com.project.tableforyou.utils.cookie.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import static com.project.tableforyou.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;

@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshToken = cookieUtil.getCookie(REFRESH_COOKIE_VALUE, request);

        if (refreshToken == null) {
            throw new RefreshTokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        refreshTokenService.delete(refreshToken);       // 로그아웃 시 redis에서 refreshToken 삭제
    }

}