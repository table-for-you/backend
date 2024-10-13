package com.project.tableforyou.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class ReviewUpdateDto {

    @Schema(description = "이전 별점", example = "3.0")
    private double preRating;

    @Min(value = 0, message = "최저 점수는 0점입니다.")
    @Max(value = 5, message = "최대 점수는 5점입니다.")
    @Schema(description = "새로운 별점", example = "3.5")
    private double rating;

    @Schema(description = "리뷰 내용 수정", example = "맛있어요!")
    private String content;
}
