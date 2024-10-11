package com.project.tableforyou.domain.common.service;

import com.project.tableforyou.domain.image.repository.ImageRepository;
import com.project.tableforyou.domain.like.repository.LikeRepository;
import com.project.tableforyou.domain.menu.repository.MenuRepository;
import com.project.tableforyou.domain.notification.repository.NotificationRepository;
import com.project.tableforyou.domain.notification.service.NotificationService;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.common.s3.service.S3Service;
import com.project.tableforyou.domain.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssociatedEntityService {

    private final LikeRepository likeRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final VisitRepository visitRepository;
    private final ImageRepository imageRepository;
    private final NotificationRepository notificationRepository;
    private final S3Service s3Service;

    /* 회원을 FK로 가지는 데이터 지우기 */
    public void deleteAllByUserId(Long userId) {

        likeRepository.deleteByUserId(userId);
        likeRepository.deleteRestaurantLikeByUserId(userId);
        visitRepository.deleteByVisitorId(userId);
        visitRepository.deleteRestaurantVisitorByUserId(userId);
        notificationRepository.deleteByUserId(userId);

        List<Restaurant> restaurants = restaurantRepository.findByUser_Id(userId);
        List<String> imageUrls = menuRepository.findMenuImagesByUserId(userId);

        restaurants.forEach(r -> {
            if (r.getMainImage() != null)
                s3Service.deleteImage(r.getMainImage());
            r.getImages().forEach(i -> s3Service.deleteImage(i.getUrl()));
        });
        imageUrls.forEach(s3Service::deleteImage);

        menuRepository.deleteRestaurantMenuByUserId(userId);
        imageRepository.deleteRestaurantImageByUserId(userId);
        restaurantRepository.deleteByUserId(userId);

    }

    /* 가게를 FK로 가지는 데이터 지우기 */
    public void deleteAllByRestaurantId(Long restaurantId) {

        likeRepository.deleteByRestaurantId(restaurantId);
        visitRepository.deleteByRestaurantId(restaurantId);

        List<String> images = imageRepository.findImageUrlsByRestaurantId(restaurantId);
        String mainImage = restaurantRepository.findMainImageById(restaurantId);
        List<String> menuImages = menuRepository.findMenuImagesByRestaurantId(restaurantId);

        if (mainImage != null)
            s3Service.deleteImage(mainImage);

        images.forEach(s3Service::deleteImage);
        menuImages.forEach(s3Service::deleteImage);

        menuRepository.deleteByRestaurantId(restaurantId);
        imageRepository.deleteByRestaurantId(restaurantId);
    }
}
