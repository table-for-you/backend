package com.project.tableforyou.domain.menu.service;

import com.project.tableforyou.domain.menu.dto.MenuRequestDto;
import com.project.tableforyou.domain.menu.dto.MenuResponseDto;
import com.project.tableforyou.domain.menu.entity.Menu;
import com.project.tableforyou.domain.menu.repository.MenuRepository;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.repository.RestaurantRepository;
import com.project.tableforyou.handler.exceptionHandler.CustomException;
import com.project.tableforyou.handler.exceptionHandler.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    /* 메뉴 추가 */
    @Transactional
    public Long save(String restaurantName, MenuRequestDto dto) {

        log.info("Creating menu");
        Restaurant restaurant = restaurantRepository.findByName(restaurantName).orElseThrow(() ->
                new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        dto.setRestaurant(restaurant);
        Menu menu = dto.toEntity();
        menuRepository.save(menu);

        log.info("Menu saved with ID: {}", menu.getId());
        return menu.getId();
    }

    /* 메뉴 리스트 페이징 */
    @Transactional(readOnly = true)
    public Page<MenuResponseDto> menuPageList(String restaurant, Pageable pageable) {

        log.info("Finding all menus with restaurant: {}", restaurant);
        Page<Menu> menus = menuRepository.findByRestaurantName(restaurant, pageable);
        return menus.map(MenuResponseDto::new);
    }

    /* 메뉴 검색 */
    @Transactional(readOnly = true)
    public Page<MenuResponseDto> menuPageSearchList(String restaurant, String searchKeyword, Pageable pageable) {

        log.info("Find all menus with Restaurant: {} and keyword: {}", restaurant, searchKeyword);
        Page<Menu> menus = menuRepository.findByRestaurantNameAndNameContaining(restaurant, searchKeyword, pageable);
        return menus.map(MenuResponseDto::new);
    }

    /* 메뉴 업데이트 */
    @Transactional
    public void update(String restaurant, Long id, MenuRequestDto dto) {

        log.info("Updating menu with ID: {}", id);
        Menu menu = menuRepository.findByRestaurantNameAndId(restaurant, id).orElseThrow(() ->
                new CustomException(ErrorCode.MENU_NOT_FOUND));
        menu.update(dto.getName(), dto.getPrice());
        log.info("Menu updated successfully");
    }

    /* 메뉴 삭제 */
    @Transactional
    public void delete(String restaurant, Long id) {

        log.info("Deleting menu with ID: {}", id);
        Menu menu = menuRepository.findByRestaurantNameAndId(restaurant, id).orElseThrow(() ->
                new CustomException(ErrorCode.MENU_NOT_FOUND));
        menuRepository.delete(menu);
        log.info("Menu deleted successfully");
    }
}