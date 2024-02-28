package com.project.tableforyou.domain.entity;

import com.project.tableforyou.domain.dto.RestaurantDto;
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

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotBlank
    private String tel;

    @Column(length = 50)
    private String description;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Menu> menus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER)
    @OrderBy("id asc")  // 예약자 순서대로 정렬
    private List<Reservation> reservations;

    public void update(RestaurantDto.UpdateRequest dto) {
        this.time = dto.getTime();
        this.name = dto.getName();
        this.location = dto.getLocation();
        this.tel = dto.getTel();
        this.description = dto.getDescription();
    }

    public void updateRating(double rating, int rating_num) {
        this.rating = rating;
        this.rating_num = rating_num;
    }
}
