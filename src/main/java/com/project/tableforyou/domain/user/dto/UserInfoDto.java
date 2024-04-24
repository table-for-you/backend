package com.project.tableforyou.domain.user.dto;

import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserInfoDto {

    private final String name;
    private final String nickname;
    private final Role role;

    public UserInfoDto(User user) {
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.role = user.getRole();
    }
}
