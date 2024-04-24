package com.project.tableforyou.domain.user.service;

import com.project.tableforyou.aop.annotation.VerifyAuthentication;
import com.project.tableforyou.domain.user.dto.PasswordDto;
import com.project.tableforyou.domain.user.dto.UserRequestDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /* 회원 추가 */
    @Transactional
    public Long create(UserRequestDto dto) {

        log.info("Creating user with username: {}", dto.getUsername());
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        User user = dto.toEntity();

        userRepository.save(user);

        log.info("User created with ID: {}", user.getId());
        return user.getId();
    }

    /* 회원 불러오기 */
    @Transactional(readOnly = true)
    public UserResponseDto findByUsername(String username) {

        log.info("Finding user by username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    /* 회원 업데이트 */
    @VerifyAuthentication
    @Transactional
    public void update(String username, UserUpdateDto dto) {

        log.info("Updating user with username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));

        user.update(dto.getNickname(), dto.getPassword());
        log.info("User updated successfully with username: {}", username);
    }

    /* 회원 삭제 */
    @Transactional
    public void delete(String username) {

        log.info("Deleting user with username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
        log.info("User deleted successfully with username: {}", username);
    }

    /* 비밀번호 검사 */
    @Transactional(readOnly = true)
    public boolean checkPass(String username, PasswordDto passwordDto) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        return bCryptPasswordEncoder.matches(passwordDto.getPassword(), user.getPassword());
    }

    /* 아이디 중복 확인 */
    public Object existsByUsername(String username) {
        log.info("Checking if user exists by username: {}", username);
        if(!username.matches("^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$")) {
            return "아이디는 특수문자를 제외한 4~20자리여야 합니다.";
        }
        return userRepository.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    public Object existsByNickname(String nickname) {
        log.info("Checking if user exists by nickname: {}", nickname);
        if (!nickname.matches("^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$")) {
            return "닉네임은 특수문자를 제외한 2~10자리여야 합니다.";
        }
        return userRepository.existsByNickname(nickname);
    }
}