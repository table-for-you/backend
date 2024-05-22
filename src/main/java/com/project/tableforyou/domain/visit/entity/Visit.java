package com.project.tableforyou.domain.visit.entity;

import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@Entity
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_time")
    private String visitTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id")
    private User visitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Builder
    public Visit(User visitor, Restaurant restaurant) {
        this.visitor = visitor;
        this.restaurant = restaurant;
        visitTime = LocalDateTime.now().format((DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
    }

}
