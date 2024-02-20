package com.project.tableforyou.service;

import com.project.tableforyou.domain.dto.RestaurantDto;
import com.project.tableforyou.domain.entity.Restaurant;
import com.project.tableforyou.domain.entity.User;
import com.project.tableforyou.repository.RestaurantRepository;
import com.project.tableforyou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    /* 가게 create */
    @Transactional
    public Long save(Long user_id, RestaurantDto.Request dto) {
        User user = userRepository.findById(user_id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. id: " + user_id));

        dto.setUser(user);
        Restaurant restaurant = dto.toEntity();
        restaurantRepository.save(restaurant);

        return restaurant.getId();
    }

    /* 가게 읽기 */
    @Transactional(readOnly = true)
    public RestaurantDto.Response findDtoById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + id));
        return new RestaurantDto.Response(restaurant);
    }

    @Transactional(readOnly = true)
    public Restaurant findById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + id));
        return restaurant;
    }

    /* 가게 리스트 페이징 */
    @Transactional(readOnly = true)
    public Page<RestaurantDto.Response> RestaurantPageList(Pageable pageable) {
        Page<Restaurant> stores = restaurantRepository.findAll(pageable);
        return stores.map(RestaurantDto.Response::new);
    }

    /* 가게 검색 || 가게 소개 검색 페이징 */
    @Transactional(readOnly = true)
    public Page<RestaurantDto.Response> RestaurantPageSearchList(String searchKeyword1, String searchKeyword2, Pageable pageable) {
        Page<Restaurant> stores = restaurantRepository.findByNameContainingOrDescriptionContaining(searchKeyword1, searchKeyword2, pageable);
        return stores.map(RestaurantDto.Response::new);
    }

    /* 가게 좌석 업데이트 */
    @Transactional
    public void updateUsedSeats(Long id, int value) {    // 가게에 user를 추가해야 하지 않나? 그리고 인원이 줄면 어떻게 user을 없애지? 그리고 예약자를 줄이고 여기로 다시 보내야하는데
        restaurantRepository.updateUsedSeats(id, value);
    }

    /* 좋아요 업데이트 */
    @Transactional
    public void updateLikeCount(Long id, int value) {
        restaurantRepository.updateLikeCount(id, value);
    }

    /* 평점 업데이트 */
    @Transactional
    public void updateRating(Long id, double rating) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + id));
        double before_rating = restaurant.getRating();
        int now_ratingNum = restaurant.getRating_num() + 1;

        double now_rating = 0.0;
        if(now_ratingNum == 1)
            now_rating = rating;
        else
            now_rating = before_rating + (rating - before_rating) / now_ratingNum;  // 누적 평균 공식.


        restaurant.updateRating(now_rating, now_ratingNum);
    }

    /* 가게 삭제 */
    @Transactional
    public void delete(Long id) {         // 다른 사용자가 삭제하는 경우 확인해보기. 만약 그런다면 findByUserIdAndId 사용. 그냥 권한 설정 하면 될듯?
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + id));
        restaurantRepository.delete(restaurant);
    }

    /* 가게 수정 */
    @Transactional
    public void update(Long id, RestaurantDto.Request dto) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + id));
        restaurant.update(dto);
    }
}