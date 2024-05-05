package com.project.tableforyou.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MenuUpdateDto {

    @NotBlank
    private String name;
    @NotBlank
    private String price;
}
