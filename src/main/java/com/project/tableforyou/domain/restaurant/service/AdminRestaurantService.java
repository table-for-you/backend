package com.project.tableforyou.domain.restaurant.service;

import com.project.tableforyou.domain.common.service.AssociatedEntityService;
import com.project.tableforyou.domain.notification.service.NotificationService;
import com.project.tableforyou.domain.restaurant.dto.PendingRestaurantDetailsDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantManageDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminRestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final NotificationService notificationService;
    private final AssociatedEntityService associatedEntityService;

    /* 등록 처리 중인 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantManageDto> readPendingRestaurant(Pageable pageable) {

        Page<Restaurant> restaurants = restaurantRepository.findByStatus(RestaurantStatus.PENDING, pageable);
        return restaurants.map(RestaurantManageDto::new);
    }

    /* 등록 처리 중인 가게 자세히 보기 */
    @Transactional(readOnly = true)
    public PendingRestaurantDetailsDto readPendingDetailsInfo(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
        return new PendingRestaurantDetailsDto(restaurant);
    }

    /* 등록된 전체 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantManageDto> readApprovedRestaurant(Pageable pageable) {

        Page<Restaurant> restaurants = restaurantRepository.findByStatus(RestaurantStatus.APPROVED, pageable);
        return restaurants.map(RestaurantManageDto::new);
    }

    /* 등록된 가게 중 사장 닉네임으로 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantManageDto> readApprovedRestaurantByOwnerName(String ownerName, Pageable pageable) {

        Page<Restaurant> restaurants =
                restaurantRepository.findByStatusAndUser_Nickname(RestaurantStatus.APPROVED, ownerName, pageable);
        return restaurants.map(RestaurantManageDto::new);
    }

    /* 등록된 가게 중 가게 이름으로 가게 불러오기 */
    @Transactional(readOnly = true)
    public Page<RestaurantManageDto> readApprovedRestaurantByRestaurantName(String restaurantName, Pageable pageable) {

        Page<Restaurant> restaurants =
                restaurantRepository.findByStatusAndNameContaining(RestaurantStatus.APPROVED, restaurantName, pageable);
        return restaurants.map(RestaurantManageDto::new);
    }

    /* 가게 등록하기 */
    @Transactional
    public void updateRestaurantStatus(Long restaurantId, RestaurantStatus status) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.statusUpdate(status);
        notificationService.createNotification(status, restaurantId, restaurant.getUser());
    }

    /* 가게 삭제하기 */
    @Transactional
    public void deleteRestaurant(Long restaurantId) {

        associatedEntityService.deleteAllByRestaurantId(restaurantId);
        restaurantRepository.deleteById(restaurantId);
    }
}
