package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantRequestDto {

    private int usedSeats;
    private int totalSeats;
    @NotBlank
    private String time;
    @NotBlank
    private String username;
    @NotBlank
    private String name;
    @NotBlank
    private String location;
    @NotBlank
    private String tel;

    private String description;
    private User user;

    /* dto -> Entity */
    public Restaurant toEntity() {
        return Restaurant.builder()
                .usedSeats(usedSeats)
                .totalSeats(totalSeats)
                .time(time)
                .username(username)
                .name(name)
                .location(location)
                .tel(tel)
                .description(description)
                .user(user)
                .build();
    }
}
