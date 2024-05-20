package com.project.tableforyou.domain.user.service;

import com.project.tableforyou.aop.annotation.VerifyAuthentication;
import com.project.tableforyou.domain.user.dto.PasswordDto;
import com.project.tableforyou.domain.user.dto.SignUpDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.dto.UserUpdateDto;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final String USERNAME_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$";
    private static final String NICKNAME_PATTERN = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$";

    /* 회원 추가 */
    @Transactional
    public Long signUp(SignUpDto signUpDto) {

        log.info("Creating user with username: {}", signUpDto.getUsername());
        signUpDto.setPassword(bCryptPasswordEncoder.encode(signUpDto.getPassword()));
        User user = signUpDto.toEntity();

        userRepository.save(user);

        log.info("User created with ID: {}", user.getId());
        return user.getId();
    }

    /* 회원 불러오기 */
    @Transactional(readOnly = true)
    public UserResponseDto readUser(String username) {

        log.info("Finding user by username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    /* 회원 업데이트 */
    @VerifyAuthentication
    @Transactional
    public void updateUser(String username, UserUpdateDto userUpdateDto) {

        log.info("Updating user with username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));
        userUpdateDto.setPassword(bCryptPasswordEncoder.encode(userUpdateDto.getPassword()));

        user.update(userUpdateDto.getNickname(), userUpdateDto.getPassword());
        log.info("User updated successfully with username: {}", username);
    }

    /* 회원 삭제 */
    @Transactional
    public void deleteUser(String username) {

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
    public boolean existsByUsername(String username) {
        log.info("Checking if user exists by username: {}", username);
        if(!username.matches(USERNAME_PATTERN)) {
            throw new CustomException(ErrorCode.INVALID_USERNAME_PATTERN);
        }
        return userRepository.existsByUsername(username);
    }

    /* 닉네임 중복 확인 */
    public boolean existsByNickname(String nickname) {
        log.info("Checking if user exists by nickname: {}", nickname);
        if (!nickname.matches(NICKNAME_PATTERN)) {
            throw new CustomException(ErrorCode.INVALID_NICKNAME_PATTERN);
        }
        return userRepository.existsByNickname(nickname);
    }
}