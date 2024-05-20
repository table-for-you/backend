package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
public class RestaurantRequestDto {

    private int totalSeats;
    @NotBlank(message = "영업 시간은 필수 입력 값입니다.")
    private String time;
    @NotBlank(message = "가게 이름은 필수 입력 값입니다.")
    private String name;
    @NotNull(message = "지역은 필수 입력 값입니다.")
    private Region region;
    @NotBlank(message = "위치 정보는 필수 입력 값입니다.")
    private String location;
    private String tel;
    private String description;
    private User user;

    /* dto -> Entity */
    public Restaurant toEntity() {
        return Restaurant.builder()
                .totalSeats(totalSeats)
                .time(time)
                .name(name)
                .region(region)
                .location(location)
                .status(RestaurantStatus.PENDING)
                .tel(tel)
                .description(description)
                .user(user)
                .build();
    }
}
