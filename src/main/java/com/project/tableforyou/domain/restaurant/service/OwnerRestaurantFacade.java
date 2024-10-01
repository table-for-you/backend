package com.project.tableforyou.domain.restaurant.service;

import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantResponseDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.visit.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerRestaurantFacade {

    private final OwnerRestaurantService ownerRestaurantService;

    public Long createRestaurant(String username, RestaurantRequestDto dto, MultipartFile mainImage, List<MultipartFile> subImages) {
        return ownerRestaurantService.saveRestaurant(username, dto, mainImage, subImages);
    }

    public List<RestaurantNameDto> getRestaurantsByOwner(String username) {
        return ownerRestaurantService.findByRestaurantOwner(username);
    }

    public List<RestaurantNameDto> getRejectedRestaurants(String username) {
        return ownerRestaurantService.findByRejectedRestaurant(username);
    }

    public void updateMainImage(Long restaurantId, MultipartFile mainImage) {
        ownerRestaurantService.updateMainImage(restaurantId, mainImage);
    }

    public void updateSubImages(Long restaurantId, List<String> deleteImageUrls, List<MultipartFile> newImages) {
        ownerRestaurantService.updateSubImages(restaurantId, deleteImageUrls, newImages);
    }

    public void updateRestaurant(Long restaurantId, RestaurantUpdateDto restaurantUpdateDto) {
        ownerRestaurantService.updateRestaurant(restaurantId, restaurantUpdateDto);
    }

    public void deleteRestaurant(Long restaurantId) {
        ownerRestaurantService.deleteRestaurant(restaurantId);
    }
}
