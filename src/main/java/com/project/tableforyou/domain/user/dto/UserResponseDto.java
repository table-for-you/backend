package com.project.tableforyou.domain.user.dto;

import com.project.tableforyou.domain.Role;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.user.entity.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserResponseDto {

    private final Long id;
    private final String name;
    private final String username;
    private final String email;
    private final String nickname;
    private final String age;

    private final Role role;
    private final String created_time;
    private final String modified_time;
    private final List<RestaurantResponseDto> restaurants;

    /* Entity -> dto */
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.age = user.getAge();
        this.role = user.getRole();
        this.created_time = user.getCreated_time();
        this.modified_time = user.getModified_time();
        this.restaurants = user.getRestaurants().stream().map(RestaurantResponseDto::new).collect(Collectors.toList());
    }
}
