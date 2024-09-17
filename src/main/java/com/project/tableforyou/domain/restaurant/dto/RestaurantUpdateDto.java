package com.project.tableforyou.domain.restaurant.dto;

import com.project.tableforyou.domain.restaurant.entity.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@Schema(name = "RestaurantUpdateDto", description = "가게 업데이트 요청 DTO")
public class RestaurantUpdateDto {

    @Schema(description = "가게 총 좌석", example = "25")
    @NotNull(message = "총 좌석은 필수 입력입니다.")
    private int totalSeats;
    @Schema(description = "가게 영업 시간", example = "09:00 ~ 19:00")
    @NotBlank(message = "영업 시간은 필수 입력 값입니다.")
    private String time;
    @Schema(description = "가게 이름", example = "햄버거 가게")
    @NotBlank(message = "가게 이름은 필수 입력 값입니다.")
    private String name;
    @Schema(description = "가게 지역", example = "SEOUL")
    @NotNull(message = "지역은 필수 입력 값입니다.")
    private Region region;
    @Schema(description = "가게 주소", example = "서울 강남구")
    @NotBlank(message = "위치 정보는 필수 입력 값입니다.")
    private String location;
    @Schema(description = "가게 전화번호", example = "02-123-4567")
    private String tel;
    @Schema(description = "가게 설명", example = "햄버거집 가게입니다.")
    private String description;
    @Schema(description = "음식 유형", example = "양식")
    private String foodType;
    @Schema(description = "주차 가능 유무", example = "true")
    private boolean isParking;
    @Schema(description = "위도", example = "128.823732447178")
    private double latitude;
    @Schema(description = "경도", example = "35.91401799249858")
    private double longitude;
}
