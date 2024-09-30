package com.project.tableforyou.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@Schema(name = "FcmTokenRequestDto", description = "FcmToken 요청 DTO")
public class FcmTokenRequestDto {

    @Schema(description = "fcmToken", example = "fcm-token")
    private String fcmToken;
}
