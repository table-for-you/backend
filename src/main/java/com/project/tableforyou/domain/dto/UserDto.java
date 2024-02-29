package com.project.tableforyou.domain.dto;

import com.project.tableforyou.config.oauth.provider.OAuth2UserInfo;
import com.project.tableforyou.domain.entity.Role;
import com.project.tableforyou.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

public class UserDto {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
        private String nickname;
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$", message = "아이디는 특수문자를 제외한 4~20자리여야 합니다.")
        private String username;
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password;
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;
        @NotBlank(message = "나이는 필수 입력 값입니다.")
        private String age;
        private String provider;
        private String providerId;
        private Role role;

        /* dto -> Entity */
        public User toEntity() {
            User user = User.builder()
                    .name(name)
                    .nickname(nickname)
                    .username(username)
                    .password(password)
                    .email(email)
                    .age(age)
                    .role(role)
                    .build();

            return user;
        }

        public User toEntity(OAuth2UserInfo oAuth2UserInfo, BCryptPasswordEncoder bCryptPasswordEncoder) {
            User user = User.builder()
                    .name(oAuth2UserInfo.getName())
                    .nickname(oAuth2UserInfo.getNickname())
                    .username(oAuth2UserInfo.getUsername())
                    .password(bCryptPasswordEncoder.encode(oAuth2UserInfo.getName()))
                    .email(oAuth2UserInfo.getEmail())
                    .role(Role.USER)
                    .build();

            return user;
        }
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {     // 업데이트 전용 DTO

        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
        private String nickname;
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password;
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;
        private int age;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String name;
        private final String username;
        private final String email;
        private final String nickname;
        private final String age;
        private final String provider;
        private final String providerId;
        private final Role role;
        private final String created_time;
        private final String modified_time;
        private final Long reservation_id;
        //private final List<RestaurantDto.Response> stores;

        /* Entity -> dto */
        public Response(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.age = user.getAge();
            this.role = user.getRole();
            this.provider = user.getProvider();
            this.providerId = user.getProviderId();
            this.created_time = user.getCreated_time();
            this.modified_time = user.getModified_time();
            this.reservation_id = (user.getReservation() != null) ? user.getReservation().getId() : null;
            //this.stores = user.getRestaurants().stream().map(RestaurantDto.Response::new).collect(Collectors.toList());
        }
    }
}