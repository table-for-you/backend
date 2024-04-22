package com.project.tableforyou.domain.menu.dto;

import com.project.tableforyou.domain.menu.entity.Menu;
import lombok.Getter;

@Getter
public class MenuResponseDto {

    private final String name;
    private final String price;
    private final String created_time;
    private final String modified_time;

    /* Entity -> dto */
    public MenuResponseDto(Menu menu) {
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.created_time = menu.getCreated_time();
        this.modified_time = menu.getModified_time();
    }
}
