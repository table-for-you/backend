package com.project.tableforyou.domain.restaurant.entity;

import com.project.tableforyou.domain.BaseTimeEntity;
import com.project.tableforyou.domain.image.entity.Image;
import com.project.tableforyou.domain.like.entity.Like;
import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.visit.entity.Visit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Restaurant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int usedSeats;

    @NotNull
    private int totalSeats;

    @NotNull
    private double rating;

    // 평점 참여 인원
    private int ratingNum;

    // 영업시간
    @NotNull
    private String time;

    @Enumerated(EnumType.STRING)
    private RestaurantStatus status;

    @NotNull
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Region region;

    @NotNull
    private String location;

    private double latitude;
    private double longitude;

    private String tel;

    @Column(length = 100)
    private String description;

    @Column(name = "main_image")
    private String mainImage;

    @Enumerated(EnumType.STRING)
    private FoodType foodType;

    @Column(name = "is_parking")
    private boolean isParking;

    @OneToMany(mappedBy = "restaurant")
    private List<Image> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "restaurant")
    private List<Like> likes;

    public void update(RestaurantUpdateDto dto) {
        this.totalSeats = dto.getTotalSeats();
        this.time = dto.getTime();
        this.name = dto.getName();
        this.region = dto.getRegion();
        this.location = dto.getLocation();
        this.tel = dto.getTel();
        this.description = dto.getDescription();
        this.foodType = dto.getFoodType();
        this.isParking = dto.isParking();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
    }

    public void addMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public void statusUpdate(RestaurantStatus status) {
        this.status = status;
    }

    public void updateRating(double rating, int ratingNum) {
        this.rating = rating;
        this.ratingNum = ratingNum;
    }

    public void updateRating(double rating) {
        this.rating = rating;
    }

    public void updateStatus(RestaurantStatus status) {
        this.status = status;
    }
}
