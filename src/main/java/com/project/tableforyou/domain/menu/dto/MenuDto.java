package com.project.tableforyou.domain.menu.dto;

import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class MenuDto {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

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

    @Getter
    public static class Response {
        private final String name;
        private final String price;
        private final Long store_id;
        private final String created_time;
        private final String modified_time;

        /* Entity -> dto */
        public Response(Menu menu) {
            this.name = menu.getName();
            this.price = menu.getPrice();
            this.store_id = menu.getRestaurant().getId();
            this.created_time = menu.getCreated_time();
            this.modified_time = menu.getModified_time();
        }
    }
}
