package com.project.tableforyou.domain.user.entity;

import com.project.tableforyou.domain.BaseTimeEntity;
import com.project.tableforyou.domain.like.entity.Like;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(unique = true, length = 8)
    private String nickname;

    @NotBlank
    @Column(unique = true, length = 20)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String age;

    private String provider;
    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Restaurant> restaurants;

    @OneToMany(mappedBy = "user")
    private List<Like> likes;

    /* 계정 잠금을 위한 필드 */
    private int loginAttempt;
    private LocalDateTime lockTime;


    public void update(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // 이미 있을 경우 최근 로그인 시간만 업데이트.
    public User updateModifiedDateIfUserExists() {
        this.onPreUpdate();
        return this;
    }

    /* 로그인 실패 횟수 초기화 */
    public void resetLoginAttempt() {
        this.loginAttempt = 0;
    }

    /* 잠긴 시간 저장 */
    public void updateLockTime() {
        this.lockTime = LocalDateTime.now();
    }

    /* 로그인 잠금 해제 */
    public void acceptLogin() {
        this.lockTime = null;
    }
}

