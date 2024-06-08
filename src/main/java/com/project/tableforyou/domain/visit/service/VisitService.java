package com.project.tableforyou.domain.visit.service;

import com.project.tableforyou.domain.restaurant.dto.RestaurantInfoDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.domain.visit.dto.VisitResponseDto;
import com.project.tableforyou.domain.visit.entity.Visit;
import com.project.tableforyou.domain.visit.repository.VisitRepository;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    /* 사용자 방문 가게 저장 */
    @Transactional
    public void saveVisitRestaurant(String username, Long restaurantId) {

        User visitor = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        Visit visit = Visit.builder()
                .visitor(visitor)
                .restaurant(restaurant)
                .build();

        visitRepository.save(visit);
    }

    /* 사용자가 방문한 가게 불러오기 */
    @Transactional(readOnly = true)
    public List<VisitResponseDto> userVisitRestaurants(String username) {

        User visitor = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Visit> visits = visitRepository.findByVisitor(visitor);

        return visits.stream().map(VisitResponseDto::new).collect(Collectors.toList());
    }

    /* 방문 가게 삭제 */
    @Transactional
    public void deleteVisitRestaurant(String username, Long restaurantId) {

        visitRepository.deleteByVisitor_UsernameAndRestaurant_Id(username, restaurantId);
    }
}
