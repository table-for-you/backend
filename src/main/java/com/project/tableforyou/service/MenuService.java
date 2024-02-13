package com.project.tableforyou.service;

import com.project.tableforyou.domain.dto.MenuDto;
import com.project.tableforyou.domain.entity.Menu;
import com.project.tableforyou.domain.entity.Restaurant;
import com.project.tableforyou.repository.MenuRepository;
import com.project.tableforyou.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    /* 메뉴 추가 */
    @Transactional
    public Long save(Long reservation_id, MenuDto.Request dto) {
        Restaurant restaurant = restaurantRepository.findById(reservation_id).orElseThrow(() ->
                new IllegalArgumentException("해당 가게가 존재하지 않습니다. id: " + reservation_id));

        dto.setRestaurant(restaurant);
        Menu menu = dto.toEntity();
        menuRepository.save(menu);
        return menu.getId();
    }

    /* 메뉴 리스트 페이징 */
    @Transactional(readOnly = true)
    public Page<MenuDto.Response> menuPageList(Long restaurant_id, Pageable pageable) {
        Page<Menu> menus = menuRepository.findByRestaurantId(restaurant_id, pageable);
        return menus.map(MenuDto.Response::new);
    }

    /* 메뉴 검색 */
    @Transactional(readOnly = true)
    public Page<MenuDto.Response> menuPageSearchList(Long restaurant_id,String searchKeyword, Pageable pageable) {
        Page<Menu> menus = menuRepository.findByRestaurantIdAndNameContaining(restaurant_id, searchKeyword, pageable);
        return menus.map(MenuDto.Response::new);
    }

    /* 메뉴 업데이트 */
    @Transactional
    public void update(Long id, MenuDto.Request dto) {
        Menu menu = menuRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 메뉴가 존재하지 않습니다. id: " + id));
        menu.update(dto.getName(), dto.getPrice());
    }

    /* 메뉴 삭제 */
    @Transactional
    public void delete(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 메뉴가 존재하지 않습니다. id: " + id));
        menuRepository.delete(menu);
    }
}
