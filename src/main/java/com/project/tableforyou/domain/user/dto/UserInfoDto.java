package com.project.tableforyou.domain.user.dto;

import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserInfoDto {

    private final Long id;
    private final String nickname;
    private final Role role;

    public UserInfoDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
    }
}
