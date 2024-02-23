package com.project.tableforyou.service;

import com.project.tableforyou.domain.dto.MenuDto;
import com.project.tableforyou.domain.entity.Menu;
import com.project.tableforyou.domain.entity.Restaurant;
import com.project.tableforyou.repository.MenuRepository;
import com.project.tableforyou.repository.RestaurantRepository;
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
    public Long save(Long reservation_id, MenuDto.Request dto) {

        log.info("Creating menu");
        Restaurant restaurant = restaurantRepository.findById(reservation_id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + reservation_id));

        dto.setRestaurant(restaurant);
        Menu menu = dto.toEntity();
        menuRepository.save(menu);

        log.info("Menu saved with ID: {}", menu.getId());
        return menu.getId();
    }

    /* 메뉴 리스트 페이징 */
    @Transactional(readOnly = true)
    public Page<MenuDto.Response> menuPageList(Long restaurant_id, Pageable pageable) {

        log.info("Finding all menus with restuarant ID: {}", restaurant_id);
        Page<Menu> menus = menuRepository.findByRestaurantId(restaurant_id, pageable);
        return menus.map(MenuDto.Response::new);
    }

    /* 메뉴 검색 */
    @Transactional(readOnly = true)
    public Page<MenuDto.Response> menuPageSearchList(Long restaurant_id, String searchKeyword, Pageable pageable) {

        log.info("Find all menus with Restaurant ID: {} and keyword: {}", restaurant_id, searchKeyword);
        Page<Menu> menus = menuRepository.findByRestaurantIdAndNameContaining(restaurant_id, searchKeyword, pageable);
        return menus.map(MenuDto.Response::new);
    }

    /* 메뉴 업데이트 */
    @Transactional
    public void update(Long id, MenuDto.Request dto) {

        log.info("Updating menu with ID: {}", id);
        Menu menu = menuRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 메뉴가 존재하지 않습니다. id: " + id));
        menu.update(dto.getName(), dto.getPrice());
        log.info("Menu updated successfully");
    }

    /* 메뉴 삭제 */
    @Transactional
    public void delete(Long id) {

        log.info("Deleting menu with ID: {}", id);
        Menu menu = menuRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 메뉴가 존재하지 않습니다. id: " + id));
        menuRepository.delete(menu);
        log.info("Menu deleted successfully");
    }
}
