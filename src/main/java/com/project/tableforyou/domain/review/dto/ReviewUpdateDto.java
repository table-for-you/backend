package com.project.tableforyou.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewUpdateDto {

    private double preRating;
    private double rating;
    private String content;
}
