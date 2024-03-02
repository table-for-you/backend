package com.project.tableforyou.service;

import com.project.tableforyou.domain.dto.UserDto;
import com.project.tableforyou.domain.entity.User;
import com.project.tableforyou.repository.UserRepository;
import com.project.tableforyou.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailService mailService;
    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    /* 회원 추가 */
    @Transactional
    public Long create(UserDto.Request dto) {

        log.info("Creating user with username: {}", dto.getUsername());
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        User user = dto.toEntity();

        userRepository.save(user);

        log.info("User created with ID: {}", user.getId());
        return user.getId();
    }

    /* 회원 불러오기 */
    @Transactional(readOnly = true)
    public UserDto.Response findById(Long id) {

        log.info("Finding user by ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. id: " + id));

        return new UserDto.Response(user);
    }

    /* 전체 회원 불러오기 */
    @Transactional(readOnly = true)
    public Page<UserDto.Response> userPageList(Pageable pageable) {

        log.info("Finding all users");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserDto.Response::new);
    }


    /* 회원 업데이트 */
    @Transactional
    public void update(String username, UserDto.UpdateRequest dto) {

        log.info("Updating user with username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. username: " + username));
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        if(!verifyAuthenticationByUsername(username, user.getUsername())) {
            throw new RuntimeException("권한이 없습니다.");
        } else {
            user.update(dto.getNickname(), dto.getPassword(), dto.getEmail());
            log.info("User updated successfully with username: {}", username);
        }
    }

    /* 회원 삭제 */
    @Transactional
    public void delete(String username) {

        log.info("Deleting user with username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. username: " + username));

        userRepository.delete(user);
        log.info("User deleted successfully with username: {}", username);
    }

    /* 회원가입 오류 확인 */
    public Map<String, String> validateHandler(Errors errors) {
        Map<String, String> validateResult = new HashMap<>();

        for (FieldError error: errors.getFieldErrors()) {
            validateResult.put(error.getField(), error.getDefaultMessage());
        }
        return validateResult;
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

    /* 이메일 중복 확인 */
    public boolean existsByEmail(String email) {
        log.info("Checking if user exists by email: {}", email);
        return userRepository.existsByEmail(email);
    }

    /* 자신의 권한인지 확인 */
    private boolean verifyAuthenticationByUsername(String expectedUsername, String actualUsername) {
        return actualUsername.equals(expectedUsername);
    }
}