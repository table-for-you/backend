package com.project.tableforyou.domain.user.service;

import com.project.tableforyou.domain.common.service.AssociatedEntityService;
import com.project.tableforyou.domain.user.dto.UserInfoDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.entity.Role;
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
    private final AssociatedEntityService associatedEntityService;

    /* 전체 회원 불러오기 */
    @Transactional(readOnly = true)
    public Page<UserInfoDto> userPageList(Pageable pageable) {

        log.info("Finding all users");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserInfoDto::new);
    }

    /* 회원 불러오기 */
    @Transactional(readOnly = true)
    public UserResponseDto adminReadUser(Long userId) {

        log.info("Finding user by userId: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDto(user);
    }

    /* 이름으로 회원 찾기 */
    @Transactional(readOnly = true)
    public Page<UserInfoDto> userPageListByName(String searchKeyword, Pageable pageable) {

        Page<User> users = userRepository.findByNameContaining(searchKeyword, pageable);
        return users.map(UserInfoDto::new);
    }

    /* 닉네임으로 회원 찾기 */
    @Transactional(readOnly = true)
    public Page<UserInfoDto> userPageListByNickname(String searchKeyword, Pageable pageable) {

        Page<User> users = userRepository.findByNicknameContaining(searchKeyword, pageable);
        return users.map(UserInfoDto::new);
    }

    /* 권한에 따라 회원 찾기 */
    @Transactional(readOnly = true)
    public Page<UserInfoDto> userPageListByRole(String searchKeyword, Pageable pageable) {

        Page<User> users = userRepository.findByRole(Role.valueOf(searchKeyword), pageable);
        return users.map(UserInfoDto::new);
    }

    /* 회원 삭제하기 */
    @Transactional
    public void deleteUserByAdmin(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        associatedEntityService.deleteAllLikeByUser(user);  // 회원 좋아요 삭제
        associatedEntityService.deleteAllVisitByUser(user); // 회원 방문가게 삭제

        if (user.getRole().equals(Role.OWNER)) {     // 사장이라면 회원 가게 삭제
            associatedEntityService.deleteAllRestaurantByUser(user);
        }


        userRepository.delete(user);
    }
}
