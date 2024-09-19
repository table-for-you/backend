package com.project.tableforyou.domain.restaurant.service;

import com.project.tableforyou.aop.annotation.VerifyAuthentication;
import com.project.tableforyou.domain.image.entity.Image;
import com.project.tableforyou.domain.image.repository.ImageRepository;
import com.project.tableforyou.domain.restaurant.dto.RestaurantNameDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantRequestDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantUpdateDto;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.s3.ImageType;
import com.project.tableforyou.domain.s3.service.S3Service;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.domain.common.service.AssociatedEntityService;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerRestaurantService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AssociatedEntityService associatedEntityService;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    /* 가게 create. 가게 등록 대기 상태. */
    @Transactional
    public Long saveRestaurant(String username, RestaurantRequestDto restaurantDto,
                               MultipartFile mainImage,
                               List<MultipartFile> subImages) {

        log.info("Creating Restaurant by user username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_FOUND));

        restaurantDto.setUser(user);
        Restaurant restaurant = restaurantDto.toEntity();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        if (!mainImage.isEmpty()) {
            String mainImageUrl = s3Service.uploadImage(mainImage, savedRestaurant.getId(), ImageType.RESTAURANT);
            savedRestaurant.addMainImage(mainImageUrl);
        }

        String subImageUrl;
        for (MultipartFile file : subImages) {
            if (!file.isEmpty()) {
                subImageUrl = s3Service.uploadImage(file, savedRestaurant.getId(), ImageType.RESTAURANT);
                imageRepository.save(Image.builder()
                        .url(subImageUrl)
                        .restaurant(savedRestaurant)
                        .build()
                );
            }
        }

        log.info("Restaurant created with ID: {}", restaurant.getId());
        return restaurant.getId();
    }

    /* 사장 가게 불러오기 */
    @Transactional(readOnly = true)
    public List<RestaurantNameDto> findByRestaurantOwner(String username) {

        List<Restaurant> restaurants = restaurantRepository.findByUser_Username(username);
        return restaurants.stream().map(RestaurantNameDto::new).collect(Collectors.toList());
    }

    /* 승인 거절된 가게 불러오기 */
    @Transactional(readOnly = true)
    public List<RestaurantNameDto> findByRejectedRestaurant(String username) {

        List<Restaurant> restaurants =
                restaurantRepository.findByUser_UsernameAndStatus(username, RestaurantStatus.REJECT);
        return restaurants.stream().map(RestaurantNameDto::new).collect(Collectors.toList());
    }

    /* 가게 수정 */
    @VerifyAuthentication
    @Transactional
    public void updateRestaurant(Long restaurantId, RestaurantUpdateDto restaurantUpdateDto) {

        log.info("Updating Restaurant with name: {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.update(restaurantUpdateDto);
        log.info("Restaurant updated successfully with name: {}", restaurantId);

    }

    /* 가게 메인 이미지 업데이트 */
    @VerifyAuthentication
    @Transactional
    public void updateMainImage(Long restaurantId, MultipartFile mainImage) {

        String currentMainImage = restaurantRepository.findMainImageById(restaurantId);
        s3Service.deleteImage(currentMainImage);

        String newMainImageUrl = null;
        if (!mainImage.isEmpty())
            newMainImageUrl = s3Service.uploadImage(mainImage, restaurantId, ImageType.RESTAURANT);
        
        restaurantRepository.updateMainImageById(restaurantId, newMainImageUrl);
    }

    /* 가게 서브 이미지 업데이트 */
    @VerifyAuthentication
    @Transactional
    public void updateSubImages(Long restaurantId,
                                List<String> deleteImageUrls,
                                List<MultipartFile> newImages) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (!deleteImageUrls.isEmpty()) {
            for (String deleteUrl: deleteImageUrls) {
                imageRepository.deleteByUrl(deleteUrl);
                s3Service.deleteImage(deleteUrl);
            }
        }

        String subImageUrl;
        for (MultipartFile file : newImages) {
            if (!file.isEmpty()) {
                subImageUrl = s3Service.uploadImage(file, restaurant.getId(), ImageType.RESTAURANT);
                imageRepository.save(Image.builder()
                        .url(subImageUrl)
                        .restaurant(restaurant)
                        .build()
                );
            }
        }
    }

    /* 가게 삭제 */
    @VerifyAuthentication
    @Transactional
    public void deleteRestaurant(Long restaurantId) {         // 다른 사용자가 삭제하는 경우 확인해보기. 만약 그런다면 findByUserIdAndId 사용. 그냥 권한 설정 하면 될듯?

        log.info("Deleting Restaurant with name: {}", restaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        associatedEntityService.deleteAllLikeByRestaurant(restaurant);  // 가게의 좋아요 삭제
        associatedEntityService.deleteAllMenuByRestaurant(restaurant);  // 가게의 메뉴 삭제
        associatedEntityService.deleteAllVisitByRestaurant(restaurant); // 방문객 삭제

        s3Service.deleteImage(restaurant.getMainImage());

        List<String> subImageUrls = imageRepository.findImageUrlsByRestaurantId(restaurantId);
        if (!subImageUrls.isEmpty()) {
            for (String url : subImageUrls) {
                s3Service.deleteImage(url);
            }
        }

        restaurantRepository.delete(restaurant);
        log.info("Restaurant deleted successfully with name: {}", restaurantId);
    }
}
