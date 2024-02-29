package com.project.tableforyou.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank
    @Column(unique = true)
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

    @OneToOne(mappedBy = "user")       // 예약순서는 바로 불러옴. 그래서 EAGER
    private Reservation reservation;

    public void update(String nickname, String password, String email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
    }

    // 이미 있을 경우 최근 로그인 시간만 업데이트.
    public User updateModifiedDateIfUserExists() {
        this.onPreUpdate();
        return this;
    }
}

