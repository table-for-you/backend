package com.project.tableforyou.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CodeDto {

    private final String code;
    private final LocalDateTime timestamp;
}
