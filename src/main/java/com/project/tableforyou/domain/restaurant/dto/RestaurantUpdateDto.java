package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Region;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
public class RestaurantUpdateDto {

    @NotNull(message = "총 좌석은 필수 입력입니다.")
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
    private String restaurantImage;
    private String foodType;
}
