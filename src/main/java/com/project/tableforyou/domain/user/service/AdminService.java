package com.project.tableforyou.domain.user.service;

import com.project.tableforyou.domain.common.service.AssociatedEntityService;
import com.project.tableforyou.domain.user.dto.UserInfoDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.common.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.common.handler.exceptionHandler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AssociatedEntityService associatedEntityService;

    /* 전체 회원 불러오기 */
    @Transactional(readOnly = true)
    public Page<UserInfoDto> readAllUser(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserInfoDto::new);
    }

    /* 회원 불러오기 */
    @Transactional(readOnly = true)
    public UserResponseDto readUserByAdmin(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    /* 닉네임으로 회원 찾기 */
    @Transactional(readOnly = true)
    public Page<UserInfoDto> readAllUserByNickname(String searchKeyword, Pageable pageable) {

        Page<User> users = userRepository.findByNicknameContaining(searchKeyword, pageable);
        return users.map(UserInfoDto::new);
    }

    /* 권한에 따라 회원 찾기 */
    @Transactional(readOnly = true)
    public Page<UserInfoDto> readAllUserByRole(String searchKeyword, Pageable pageable) {

        Page<User> users = userRepository.findByRole(Role.valueOf(searchKeyword), pageable);
        return users.map(UserInfoDto::new);
    }

    /* 회원 삭제하기 */
    @Transactional
    public void deleteUserByAdmin(Long userId) {

        associatedEntityService.deleteAllByUserId(userId);
        userRepository.deleteById(userId);
    }
}
