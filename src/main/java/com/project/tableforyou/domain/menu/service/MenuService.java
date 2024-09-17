package com.project.tableforyou.domain.menu.service;

import com.project.tableforyou.aop.annotation.VerifyAuthentication;
import com.project.tableforyou.domain.menu.dto.MenuRequestDto;
import com.project.tableforyou.domain.menu.dto.MenuResponseDto;
import com.project.tableforyou.domain.menu.dto.MenuUpdateDto;
import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.menu.repository.MenuRepository;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.domain.s3.ImageType;
import com.project.tableforyou.domain.s3.service.S3Service;
import com.project.tableforyou.handler.exceptionHandler.exception.CustomException;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Service s3Service;

    /* 메뉴 추가 */
    @VerifyAuthentication
    @Transactional
    public Long saveMenu(Long restaurantId, MenuRequestDto menuDto, MultipartFile menuImage) {

        log.info("Creating menu");
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        menuDto.setRestaurant(restaurant);
        Menu menu = menuDto.toEntity();
        Menu savedMenu = menuRepository.save(menu);

        if (!menuImage.isEmpty()) {
            String menuImageUrl = s3Service.uploadImage(menuImage, savedMenu.getId(), ImageType.MENU);
            savedMenu.addMenuImage(menuImageUrl);
        }

        log.info("Menu saved with ID: {}", menu.getId());
        return menu.getId();
    }

    /* 메뉴 리스트 페이징 */
    @Transactional(readOnly = true)
    public Page<MenuResponseDto> readAllMenu(Long restaurantId, Pageable pageable) {

        log.info("Finding all menus with restaurant: {}", restaurantId);
        Page<Menu> menus = menuRepository.findByRestaurantId(restaurantId, pageable);
        return menus.map(MenuResponseDto::new);
    }

    /* 메뉴 검색 */
    @Transactional(readOnly = true)
    public Page<MenuResponseDto> menuPageSearchList(Long restaurantId, String searchKeyword, Pageable pageable) {

        log.info("Find all menus with Restaurant: {} and keyword: {}", restaurantId, searchKeyword);
        Page<Menu> menus = menuRepository.findByRestaurantIdAndNameContaining(restaurantId, searchKeyword, pageable);
        return menus.map(MenuResponseDto::new);
    }

    /* 메뉴 사진 업데이트 */
    @VerifyAuthentication
    @Transactional
    public void updateMenuImage(Long menuId, MultipartFile menuImage) {

        String currentMenuImage = menuRepository.findMenuImageById(menuId);
        s3Service.deleteImage(currentMenuImage);

        String newMenuImageUrl = null;
        if (!menuImage.isEmpty())
            newMenuImageUrl = s3Service.uploadImage(menuImage, menuId, ImageType.MENU);

        menuRepository.updateMenuImageById(menuId, newMenuImageUrl);
    }

    /* 메뉴 업데이트 */
    @VerifyAuthentication
    @Transactional
    public void updateMenu(Long restaurantId, Long menuId, MenuUpdateDto menuUpdateDto) {

        log.info("Updating menu with ID: {}", menuId);
        Menu menu = menuRepository.findByRestaurantIdAndId(restaurantId, menuId).orElseThrow(() ->
                new CustomException(ErrorCode.MENU_NOT_FOUND));
        menu.update(menuUpdateDto.getName(), menuUpdateDto.getPrice());
        log.info("Menu updated successfully");
    }

    /* 메뉴 삭제 */
    @VerifyAuthentication
    @Transactional
    public void deleteMenu(Long restaurantId, Long menuId) {

        log.info("Deleting menu with ID: {}", menuId);
        Menu menu = menuRepository.findByRestaurantIdAndId(restaurantId, menuId).orElseThrow(() ->
                new CustomException(ErrorCode.MENU_NOT_FOUND));

        s3Service.deleteImage(menu.getMenuImage());
        menuRepository.delete(menu);
        log.info("Menu deleted successfully");
    }
}
