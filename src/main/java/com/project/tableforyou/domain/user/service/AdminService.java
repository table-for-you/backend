package com.project.tableforyou.domain.user.service;

import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    /* 전체 회원 불러오기 */
    @Transactional(readOnly = true)
    public Page<UserResponseDto> userPageList(Pageable pageable) {

        log.info("Finding all users");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserResponseDto::new);
    }

    /* 회원 삭제하기 */
    @Transactional
    public void deleteUser(Long user_id) {

        User user = userRepository.findById(user_id).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
