package com.project.tableforyou.domain.user.dto;

import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.user.entity.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserResponseDto {

    private final Long id;
    private final String username;
    private final String email;
    private final String nickname;
    private final String age;
    private final Role role;
    private final String createdTime;
    private final String modifiedTime;

    /* Entity -> dto */
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.age = user.getAge();
        this.role = user.getRole();
        this.createdTime = user.getCreatedTime();
        this.modifiedTime = user.getModifiedTime();
    }
}