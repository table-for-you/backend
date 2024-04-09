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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public User login(LoginDto dto) {

        User findUser = userRepository.findByUsername(dto.getUsername()).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!checkPassword(dto.getPassword(), findUser.getPassword())) {
            throw new BadCredentialsException("올바르지 않은 비밀번호 입니다.");
        }

        return findUser;
    }

    private boolean checkPassword(String actual, String expect) {

        return bCryptPasswordEncoder.matches(actual, expect);
    }
}
