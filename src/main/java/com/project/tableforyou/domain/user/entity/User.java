package com.project.tableforyou.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.tableforyou.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
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

    @Column(name = "fcm_token")
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    private Role role;

    /* 계정 잠금을 위한 필드 */
    private int loginAttempt;

    @JsonIgnore
    private LocalDateTime lockTime;


    public void update(String nickname, String age) {
        this.nickname = nickname;
        this.age = age;
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

    /* fcmToken 저장 */
    public void addFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}

