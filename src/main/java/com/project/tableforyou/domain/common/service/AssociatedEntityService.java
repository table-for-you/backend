package com.project.tableforyou.domain.common.service;

import com.project.tableforyou.domain.like.entity.Like;
import com.project.tableforyou.domain.like.repository.LikeRepository;
import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.menu.repository.MenuRepository;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.visit.entity.Visit;
import com.project.tableforyou.domain.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssociatedEntityService {

    private final LikeRepository likeRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final VisitRepository visitRepository;

    /* 회원과 관계 매핑된 좋아요 삭제(회원이 좋아요한 것) */
    @Transactional
    public void deleteAllLikeByUser(User foundUser) {

        List<Long> likeIds = foundUser.getLikes().stream()
                .map(Like::getId)
                .collect(Collectors.toList());

        if (!likeIds.isEmpty()) {
            likeRepository.deleteAllLikeByIdInQuery(likeIds);
            log.info("Associated Like deleted successfully for username: {}", foundUser.getUsername());
        }
    }

    /* 가게와 관계 매핑된 좋아요 삭제(좋아요가 적힌 가게) */
    @Transactional
    public void deleteAllLikeByRestaurant(Restaurant foundRestaurant) {

        List<Long> likeIds = foundRestaurant.getLikes().stream()
                .map(Like::getId)
                .collect(Collectors.toList());

        if (!likeIds.isEmpty()) {
            likeRepository.deleteAllLikeByIdInQuery(likeIds);
            log.info("Associated Like deleted successfully for restaurant: {}", foundRestaurant.getName());
        }
    }

    /* 회원과 관계 매핑된 가게 삭제 (회원의 가게) */
    @Transactional
    public void deleteAllRestaurantByUser(User foundUser) {

        List<Restaurant> restaurants = foundUser.getRestaurants();

        for (Restaurant restaurant : restaurants) {
            this.deleteAllVisitByRestaurant(restaurant);    // 가게 방문객 삭제하기
            this.deleteAllMenuByRestaurant(restaurant);     // 가게의 메뉴 삭제하기
        }

        List<Long> restaurantIds = restaurants.stream()
                .map(Restaurant::getId)
                .collect(Collectors.toList());

        if (!restaurantIds.isEmpty()) {
            restaurantRepository.deleteAllRestaurantByIdInQuery(restaurantIds);
            log.info("Associated restaurants deleted successfully for username: {}", foundUser.getUsername());
        }
    }

    /* 가게와 관계 매핑된 메뉴 삭제 (가게의 메뉴) */
    @Transactional
    public void deleteAllMenuByRestaurant(Restaurant restaurant) {
        List<Long> menuIds = restaurant.getMenus().stream()
                .map(Menu::getId)
                .collect(Collectors.toList());

        if (!menuIds.isEmpty()) {
            menuRepository.deleteAllMenuByIdInQuery(menuIds);
            log.info("Associated menus deleted successfully for restaurant: {}", restaurant.getName());
        }
    }

    /* 사용자가 관계 매핑된 방문한 가게(Visit) 삭제 */
    @Transactional
    public void deleteAllVisitByUser(User foundUser) {
        List<Long> visitIds = foundUser.getVisits().stream()
                .map(Visit::getId)
                .collect(Collectors.toList());

        if (!visitIds.isEmpty()) {
            visitRepository.deleteAllVisitByIdInQuery(visitIds);
            log.info("Associated visits deleted successfully for username: {}", foundUser.getUsername());
        }
    }

    /* 가게에 관계 매핑된 방문객(Visit) 삭제 */
    @Transactional
    public void deleteAllVisitByRestaurant(Restaurant foundRestaurant) {
        List<Long> visitIds = foundRestaurant.getVisits().stream()
                .map(Visit::getId)
                .collect(Collectors.toList());

        if (!visitIds.isEmpty()) {
            visitRepository.deleteAllVisitByIdInQuery(visitIds);
            log.info("Associated visits deleted successfully for restaurant: {}", foundRestaurant.getName());
        }
    }
}
