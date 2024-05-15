package com.project.tableforyou.domain.restaurant.entity;

import com.project.tableforyou.domain.BaseTimeEntity;
import com.project.tableforyou.domain.like.entity.Like;
import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private int rating_num;

    // 영업시간
    @NotNull
    private String time;

    @Enumerated(EnumType.STRING)
    private RestaurantStatus status;

    @NotNull
    private String name;

    @NotNull
    private String location;

    private String tel;

    @Column(length = 50)
    private String description;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Menu> menus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "restaurant")
    private List<Like> likes;

    public void update(RestaurantUpdateDto dto) {
        this.totalSeats = dto.getTotalSeats();
        this.time = dto.getTime();
        this.name = dto.getName();
        this.location = dto.getLocation();
        this.tel = dto.getTel();
        this.description = dto.getDescription();
    }

    /* 가게 주인 ADMIN -> USER로 변경 (가게 생성) */
    public void statusUpdate(RestaurantStatus status) {
        this.status = status;
    }

    public void updateRating(double rating, int rating_num) {
        this.rating = rating;
        this.rating_num = rating_num;
    }
}
