package com.project.tableforyou.domain.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantUpdateDto {

    @NotNull
    private int totalSeats;
    @NotBlank
    private String time;
    @NotBlank
    private String name;
    @NotBlank
    private String location;
    private String tel;
    private String description;
}
