package com.project.tableforyou.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class MenuUpdateDto {

    @NotBlank(message = "메뉴명은 필수 입력입니다.")
    private String name;
    @NotBlank(message = "가격은 필수 입력입니다.")
    private String price;
}
