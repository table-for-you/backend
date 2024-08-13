package com.project.tableforyou.auth.service;


import com.project.tableforyou.auth.dto.LoginDto;
import com.project.tableforyou.auth.dto.UserRoleDto;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.mail.service.FindPassService;
import com.project.tableforyou.token.service.RefreshTokenService;
import com.project.tableforyou.token.service.TokenBlackListService;
import com.project.tableforyou.utils.cookie.CookieUtil;
import com.project.tableforyou.utils.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.project.tableforyou.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;
import static com.project.tableforyou.utils.jwt.JwtProperties.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FindPassService findPassService;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlackListService tokenBlackListService;
    private final JwtUtil jwtUtil;
    private final static int LOCK_LIMIT_COUNT = 5;
    private final static long LOCK_TIME = 5;

    /* 로그인 메서드 */
    public User login(LoginDto dto) {

        User findUser = userRepository.findByUsername(dto.getUsername()).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        if(findUser.getLockTime() != null && !checkLockTime(findUser)) {    // 잠금 상태 확인
            throw new CustomException(ErrorCode.USER_LOCKED);
        }

        if(!checkPassword(dto.getPassword(), findUser.getPassword())) {
            userRepository.updateLoginAttempt(findUser.getUsername());

            if(findUser.getLoginAttempt() >= LOCK_LIMIT_COUNT - 1) {    // 실패 횟수 체크
                findUser.updateLockTime();
                userRepository.save(findUser);      // 정보 업데이트 위해.
            }
            throw new CustomException(ErrorCode.USER_INVALID_PASSWORD);
        }

        resetLoginAttempt(findUser);    // 로그인 성공 시, 실패 횟수 초기화
        return findUser;
    }

    /* 로그아웃 */
    public void signOut(String accessTokenInHeader, String refreshToken, HttpServletResponse response) {

        cookieUtil.deleteCookie(REFRESH_COOKIE_VALUE, response);    // 쿠키값 삭제

        refreshTokenService.delete(refreshToken);       // 로그아웃 시 redis에서 refreshToken 삭제

        String accessToken = accessTokenInHeader.substring(TOKEN_PREFIX.length()).trim();
        tokenBlackListService.save(accessToken);           // accessToken blackList에 저장
    }

    /* 아이디 찾기 메서드 */
    @Transactional(readOnly = true)
    public String findingId(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        return user.getUsername();
    }

    /* 비밀번호 찾기 */
    @Transactional
    public void findingPassword(String username, String email) {

        if (!userRepository.existsByUsernameAndEmail(username, email))
            throw new CustomException(ErrorCode.INVALID_USER_INFO);

        findPassService.sendPassToMail(username, email);
    }

    /* 비밀번호 확인 */
    private boolean checkPassword(String actual, String expect) {

        return bCryptPasswordEncoder.matches(actual, expect);
    }

    /* 잠금 시간 확인 */
    private boolean checkLockTime(User user) {

        LocalDateTime nowTime = LocalDateTime.now();
        boolean check = user.getLockTime().plusMinutes(LOCK_TIME).isBefore(nowTime);

        if(check) {
            resetLoginAttempt(user);
            user.acceptLogin();
        }

        return check;
    }

    /* 로그인 실패 횟수 초기화 */
    private void resetLoginAttempt(User user) {

        user.resetLoginAttempt();
    }

    /* 사용자 권한 찾기 */
    public UserRoleDto findUserRoleByToken(String accessToken) {

        String splitToken = accessToken.substring(TOKEN_PREFIX.length()).trim();

        String role = jwtUtil.getRole(splitToken);
        return new UserRoleDto(role);
    }
}
