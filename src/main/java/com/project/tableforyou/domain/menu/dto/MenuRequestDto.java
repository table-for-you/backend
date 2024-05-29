package com.project.tableforyou.domain.menu.dto;

import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class MenuRequestDto {

    @NotBlank(message = "메뉴명은 필수 입력입니다.")
    private String name;
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
