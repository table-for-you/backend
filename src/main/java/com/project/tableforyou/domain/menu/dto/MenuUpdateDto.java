package com.project.tableforyou.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuUpdateDto {

    @NotBlank
    private String name;
    @NotBlank
    private String price;
}
