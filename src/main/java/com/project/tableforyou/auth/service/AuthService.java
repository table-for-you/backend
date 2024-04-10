package com.project.tableforyou.auth.service;


import com.project.tableforyou.auth.dto.LoginDto;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.CustomException;
import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
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
}
