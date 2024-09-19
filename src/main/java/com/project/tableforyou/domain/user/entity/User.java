package com.project.tableforyou.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.tableforyou.domain.BaseTimeEntity;
import com.project.tableforyou.domain.Notification.entity.Notification;
import com.project.tableforyou.domain.like.entity.Like;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.visit.entity.Visit;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
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
    private Long id;

    @NotNull
    @Column(unique = true, length = 10)
    private String nickname;

    @NotNull
    @Column(unique = true, length = 20)
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String age;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Restaurant> restaurants;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Like> likes;

    @OneToMany(mappedBy = "visitor")
    @JsonIgnore
    private List<Visit> visits;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Notification> notifications;

    /* 계정 잠금을 위한 필드 */
    private int loginAttempt;
    private LocalDateTime lockTime;


    public void update(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public void updatePassword(String password) {
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

