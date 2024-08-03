package com.project.tableforyou.domain.menu.dto;

import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
@Schema(name = "MenuRequestDto", description = "메뉴 생성 요청 DTO")
public class MenuRequestDto {

    @Schema(description = "메뉴명", example = "햄버거")
    @NotBlank(message = "메뉴명은 필수 입력입니다.")
    private String name;
    @Schema(description = "메뉴 가격", example = "6,000")
    @NotBlank(message = "가격은 필수 입력입니다.")
    private String price;
    private Restaurant restaurant;

    /* dto -> Entity */
    public Menu toEntity() {
        return Menu.builder()
                .name(name)
                .price(price)
                .restaurant(restaurant)
                .build();
    }
}
