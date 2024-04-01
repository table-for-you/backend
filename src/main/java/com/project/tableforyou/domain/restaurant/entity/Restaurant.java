package com.project.tableforyou.domain.restaurant.entity;

import com.project.tableforyou.domain.BaseTimeEntity;
import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter
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
    private int likeCount;

    @NotNull
    private double rating;

    private int rating_num;

    // 영업시간
    @NotBlank
    private String time;

    // 가게 주인 변경을 위해. (ADMIN -> 가게 신청한 USER)
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    private String tel;

    @Column(length = 50)
    private String description;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Menu> menus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void update(RestaurantUpdateDto dto) {
        this.totalSeats = dto.getTotalSeats();
        this.time = dto.getTime();
        this.name = dto.getName();
        this.location = dto.getLocation();
        this.tel = dto.getTel();
        this.description = dto.getDescription();
    }

    /* 가게 주인 ADMIN -> USER로 변경 (가게 생성) */
    public void userUpdate(User user) {
        this.user = user;
    }

    public void updateRating(double rating, int rating_num) {
        this.rating = rating;
        this.rating_num = rating_num;
    }
}
