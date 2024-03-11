package com.project.tableforyou.service;

import com.project.tableforyou.domain.dto.ReservationDto;
import com.project.tableforyou.domain.entity.Reservation;
import com.project.tableforyou.domain.entity.Restaurant;
import com.project.tableforyou.domain.entity.User;
import com.project.tableforyou.repository.ReservationRepository;
import com.project.tableforyou.repository.RestaurantRepository;
import com.project.tableforyou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /* 예약자 추가 */
    @Transactional
    public Long save(String username, Long store_id) {

        log.info("Creating Reservation");
        Reservation reservation = new Reservation();

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. username: " + username));
        Restaurant restaurant = restaurantRepository.findById(store_id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + store_id));

        reservation.setUser(user);
        reservation.setRestaurant(restaurant);

        int size = restaurant.getReservations().size();  // 현재 가게에 몇명의 예약자가 있는지.
        reservation.setBooking(size+1);

        reservationRepository.save(reservation);
        log.info("Reservation created with ID: {}", reservation.getId());
        return reservation.getId();
    }

    /* 예약 읽기*/
    @Transactional(readOnly = true)
    public ReservationDto.Response findById(Long id) {

        log.info("Finding Reservation by ID: {}", id);
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당하는 예약번호가 없습니다. id" + id));
        return new ReservationDto.Response(reservation);
    }


    /* 예약자 줄어 들 때 - 시간 복잡도의 문제가 있겠지만, 모든 예약자들의 번호를 1씩 줄여야 하기 때문에 Controller에서 반복문으로 수행. */
    @Transactional      // List<Reservation> reservations = store.getReservations(); 해서 넘기기. url : /{sotre_id}/~~
    public User decreaseBooking(List<Reservation> reservations) {       // 이렇게 넘기면 잘못되면 rollback까지 됨.

        log.info("Decreasing bookings for reservations");
        User user = null;        // forEach를 사용하여 외부에서 선언된 변수를 내부에서 수정할 때에는 배열이나 컬렉션을 사용하여 값을 전달해야 함. User[] user = new User[1];
        for (Reservation reservation : reservations) {// ReservationDto.Response를 Reservation으로 바꿔서 해야함.
            reservationRepository.decreaseBooking(reservation.getId());
            // List<Reservation> reservations = getReservations(restaurant_id, 0L, null);  이미 여기서 트랜잭션은 끝나 1차캐시에 없음.
            // 그래서 위 메서드를 실행하더라도 @Transaction이기에 commit해야 데이터가 반영됨. 왜냐면 JPQL은 1차캐시에 데이터를 가져오는 것이 아니라 db를 직접 수정하는 것.
            // 그래서 1애서 -1을 하더라도 commit전까진 1이다. 그래서 아래 reservation.getBooking() == 1을 해야 원하는 결과 반영.

            //reservation.setBooking(reservation.getBooking()-1);
            if (reservation.getBooking() == 1) {
                user = reservation.getUser();
                reservationRepository.delete(reservation);      // 예약번호 1번 지우기
                log.info("Reservation with ID {} deleted", reservation.getId());
            }
        }

        log.info("Bookings decreased successfully");
        return user;
    }

    /* 예약 미루기(미루기할 시 store 예약자 수에 대한 조건 + 뒤에 있던 사람들 앞으로 당기기 - decreaseBooking) */
    @Transactional
    public void postponedGuestBooking(Long reservation_id, ReservationDto.Request dto) {

        log.info("Postponing guest booking for reservation with ID: {}", reservation_id);
        Reservation reservation = reservationRepository.findById(reservation_id).orElseThrow(() ->
                new IllegalArgumentException("해당하는 예약번호가 없습니다. id" + reservation_id));
        reservation.update(dto.getBooking());
    }

    /* 해당 가게 예약자 가져오기. 페이징처리 */
    @Transactional(readOnly = true)
    public Page<ReservationDto.Response> findByRestaurantId(Long restaurant_id, Pageable pageable) {

        log.info("Finding reservations by restaurant ID: {}", restaurant_id);
        Page<Reservation> reservations = reservationRepository.findByRestaurantId(restaurant_id, pageable);
        return reservations.map(ReservationDto.Response::new);
    }

    /* 예약자 삭제(중간사람 삭제 시 뒤에 있던 사람들 앞으로 당기기 - decreaseBooking) */
    @Transactional
    public void delete(Long reservation_id) {

        log.info("Deleting reservation with ID: {}", reservation_id);
        Reservation reservation = reservationRepository.findById(reservation_id).orElseThrow(() ->
                new IllegalArgumentException("해당하는 예약번호가 없습니다. id" + reservation_id));
        reservationRepository.delete(reservation);
        log.info("Reservation deleted successfully");
    }

    /* 예약자 List를 받기위한 메서드. */
    public List<Reservation> getReservations(Long restaurant_id, Long reservation_id, ReservationDto.Request dto) {

        Restaurant restaurant = restaurantRepository.findById(restaurant_id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + restaurant_id));
        List<Reservation> reservations = restaurant.getReservations();

        if (reservation_id == 0 && dto == null) {    // 일반적인 예약 앞당기기.
            return reservations;
        }

        Reservation before_reservation = reservationRepository.findById(reservation_id).orElseThrow(() ->
                new IllegalArgumentException("해당하는 예약번호가 없습니다. id" + reservation_id));
        List<Reservation> decreaseReservation = new ArrayList<>();

        if (dto == null) {       // 예약 삭제로 인한 뒷사람 앞당기기.
            for (Reservation reservation1 : reservations) {
                    if (reservation1.getBooking() > before_reservation.getBooking()) decreaseReservation.add(reservation1);
            }
        }

        // 예약 미루기로 인한 사이 번호 앞당기기.
        if (dto.getBooking() > restaurant.getReservations().size()
                || dto.getBooking() <= before_reservation.getBooking())   // 예약 번호가 마지막 예약 사람보다 클 때 or 자신보다 앞의 번호일 때.
            return null;
        else {
            for (Reservation reservation1 : reservations) {
                if (reservation1.getBooking() > before_reservation.getBooking() && reservation1.getBooking() <= dto.getBooking())
                    decreaseReservation.add(reservation1);
            }

            return decreaseReservation;

        }

    }

}
