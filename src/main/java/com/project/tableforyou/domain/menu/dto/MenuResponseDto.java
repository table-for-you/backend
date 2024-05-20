package com.project.tableforyou.domain.menu.dto;

import com.project.tableforyou.domain.menu.entity.Menu;
import lombok.Getter;

@Getter
public class MenuResponseDto {

    private final String name;
    private final String price;
    private final String createdTime;
    private final String modifiedTime;

    /* Entity -> dto */
    public MenuResponseDto(Menu menu) {
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.createdTime = menu.getCreatedTime();
        this.modifiedTime = menu.getModifiedTime();
    }
}
