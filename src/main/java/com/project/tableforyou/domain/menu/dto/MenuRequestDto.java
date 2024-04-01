package com.project.tableforyou.domain.menu.dto;

import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuRequestDto {

    @NotBlank
    private String name;
    @NotBlank
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
