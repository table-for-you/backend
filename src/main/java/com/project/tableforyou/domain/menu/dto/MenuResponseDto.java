package com.project.tableforyou.domain.menu.dto;

import com.project.tableforyou.domain.menu.entity.Menu;
import lombok.Getter;

@Getter
public class MenuResponseDto {

    private final Long id;
    private final String name;
    private final String price;
    private final String menuImage;
    private final String createdTime;
    private final String modifiedTime;

    /* Entity -> dto */
    public MenuResponseDto(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.menuImage = menu.getMenuImage();
        this.createdTime = menu.getCreatedTime();
        this.modifiedTime = menu.getModifiedTime();
    }
}
