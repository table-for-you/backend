package com.project.tableforyou.domain.entity;


import com.project.tableforyou.domain.dto.ReservationDto;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 최대 예약 건수 설정을 해야할 듯.
    private int booking;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public Reservation(ReservationDto.Response dto) {
        this.booking = dto.getBooking();
    }

    public void update(int booking) {
        this.booking = booking;
    }
}
