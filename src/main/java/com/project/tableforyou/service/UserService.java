package com.project.tableforyou.service;

import com.project.tableforyou.domain.dto.UserDto;
import com.project.tableforyou.domain.entity.User;
import com.project.tableforyou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /* 회원 추가 */
    @Transactional
    public Long create(UserDto.Request dto) {
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        User user = dto.toEntity();

        userRepository.save(user);

        return user.getId();
    }

    /* 회원 불러오기 */
    @Transactional(readOnly = true)
    public UserDto.Response findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. id: " + id));

        return new UserDto.Response(user);
    }

    /* 전체 회원 불러오기 */
    @Transactional(readOnly = true)
    public Page<UserDto.Response> userPageList(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserDto.Response::new);
    }


    /* 회원 업데이트 */
    @Transactional
    public void update(Long id, UserDto.Request dto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. id: " + id));
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        user.update(dto.getNickname(), dto.getPassword(), dto.getEmail());
    }

    /* 회원 삭제 */
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. id: " + id));
        userRepository.delete(user);
    }

    /* 회원가입 오류 확인 */
    public Map<String, String> validateHandler(Errors errors) {
        Map<String, String> validateResult = new HashMap<>();

        for (FieldError error: errors.getFieldErrors()) {
            String validKeyName = "valid_" + error.getField();
            validateResult.put(validKeyName, error.getDefaultMessage());
        }
        return validateResult;
    }
}
