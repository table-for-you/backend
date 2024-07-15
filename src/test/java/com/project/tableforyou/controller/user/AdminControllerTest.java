package com.project.tableforyou.controller.user;

import com.project.tableforyou.domain.restaurant.dto.PendingRestaurantDetailsDto;
import com.project.tableforyou.domain.restaurant.dto.RestaurantManageDto;
import com.project.tableforyou.domain.restaurant.entity.Region;
import com.project.tableforyou.domain.restaurant.entity.Restaurant;
import com.project.tableforyou.domain.restaurant.entity.RestaurantStatus;
import com.project.tableforyou.domain.restaurant.service.AdminRestaurantService;
import com.project.tableforyou.domain.user.controller.AdminController;
import com.project.tableforyou.domain.user.dto.UserInfoDto;
import com.project.tableforyou.domain.user.dto.UserResponseDto;
import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.service.AdminService;
import com.project.tableforyou.handler.exceptionHandler.error.ErrorCode;
import com.project.tableforyou.security.auth.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private AdminRestaurantService adminRestaurantService;

    private User user;
    private User test1;
    private User test2;
    private User test3;

    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private Restaurant restaurant3;
    private Restaurant restaurant4;
    private PrincipalDetails principalDetails;
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
                .defaultRequest(patch("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .build();

        userCreate();
        restaurantCreate();

        principalDetails = new PrincipalDetails(user);
    }

    void userCreate() {
        user = User.builder()
                .username("admin")
                .password("password")
                .role(Role.ADMIN)
                .build();

        test1 = User.builder()
                .name("user1")
                .nickname("사용자1")
                .role(Role.OWNER)
                .build();

        test2 = User.builder()
                .name("user2")
                .nickname("테스트1")
                .role(Role.USER)
                .build();

        test3 = User.builder()
                .name("test1")
                .nickname("테스트2")
                .role(Role.USER)
                .build();
    }

    void restaurantCreate() {
        restaurant1 = Restaurant.builder()
                .id(1L)
                .name("가게1")
                .time("09:00~17:00")
                .region(Region.DAEGU)
                .location("대구 중구")
                .user(test1)
                .status(RestaurantStatus.PENDING)
                .build();

        restaurant2 = Restaurant.builder()
                .id(2L)
                .name("가게2")
                .time("09:00~17:00")
                .region(Region.DAEGU)
                .location("대구 중구")
                .user(test1)
                .status(RestaurantStatus.PENDING)
                .build();

        restaurant3 = Restaurant.builder()
                .id(3L)
                .name("테스트가게1")
                .time("09:00~17:00")
                .region(Region.DAEGU)
                .location("대구 중구")
                .user(test2)
                .status(RestaurantStatus.APPROVED)
                .build();

        restaurant4 = Restaurant.builder()
                .id(4L)
                .name("테스트가게2")
                .time("09:00~17:00")
                .region(Region.DAEGU)
                .location("대구 중구")
                .user(test2)
                .status(RestaurantStatus.APPROVED)
                .build();
    }


    @Test
    @DisplayName("전체 회원 불러오기 테스트")
    void readAllUserTest() throws Exception {
        // given
        Page<UserInfoDto> users = new PageImpl<>(List.of(
                new UserInfoDto(test1),
                new UserInfoDto(test2),
                new UserInfoDto(test3)
        ));

        given(adminService.readAllUser(any(Pageable.class))).willReturn(users);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/users")
                        .with(user(principalDetails))
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "name")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("user1"))
                .andExpect(jsonPath("$.content[1].name").value("user2"))
                .andExpect(jsonPath("$.content[2].name").value("test1"));
    }

    @Test
    @DisplayName("이름으로 사용자 검색 테스트")
    void readAllUserByNameTest() throws Exception {
        // given
        Page<UserInfoDto> users = new PageImpl<>(List.of(
                new UserInfoDto(test1),
                new UserInfoDto(test2)
        ));

        given(adminService.readAllUserByName(anyString(), any(Pageable.class))).willReturn(users);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/users")
                        .with(user(principalDetails))
                        .param("type", "name")
                        .param("search-keyword", "user")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "name")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("user1"))
                .andExpect(jsonPath("$.content[1].name").value("user2"));

    }

    @Test
    @DisplayName("닉네임으로 사용자 검색 테스트")
    void readAllUserByNicknameTest() throws Exception {
        // given
        Page<UserInfoDto> users = new PageImpl<>(List.of(
                new UserInfoDto(test2),
                new UserInfoDto(test3)
        ));

        given(adminService.readAllUserByNickname(anyString(), any(Pageable.class))).willReturn(users);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/users")
                        .with(user(principalDetails))
                        .param("type", "nickname")
                        .param("search-keyword", "테스트")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "name")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nickname").value("테스트1"))
                .andExpect(jsonPath("$.content[1].nickname").value("테스트2"));

    }

    @Test
    @DisplayName("역할로 사용자 검색 테스트")
    void readAllUserByRoleTest() throws Exception {
        // given
        Page<UserInfoDto> users = new PageImpl<>(List.of(
                new UserInfoDto(test2),
                new UserInfoDto(test3))
        );

        given(adminService.readAllUserByRole(anyString(), any(Pageable.class))).willReturn(users);

        // when
        ResultActions resultActions =  mockMvc.perform(
                get("/admin/users")
                        .with(user(principalDetails))
                        .param("type", "role")
                        .param("search-keyword", "USER")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "name")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].role").value("USER"))
                .andExpect(jsonPath("$.content[1].role").value("USER"));
    }

    @Test
    @DisplayName("사용자 불러오기 실패 테스트 - 검색 유형 불일치")
    void readAllUserFailedInvalidTest() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/users")
                        .with(user(principalDetails))
                        .param("type", "no-exists")
                        .param("search-keyword", ".")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "name")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()));
    }

    @Test
    @DisplayName("어드민이 사용자 정보 불러오기 테스트")
    void readUserByAdminTest() throws Exception {
        // given
        Long userId = 1L;
        UserResponseDto userResponseDto = new UserResponseDto(test1);

        given(adminService.readUserByAdmin(userId)).willReturn(userResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/users/{userId}", userId)
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user1"))
                .andExpect(jsonPath("$.nickname").value("사용자1"));
    }

    @Test
    @DisplayName("어드민이 회원 삭제하기 테스트")
    void deleteUserByAdminTest() throws Exception {
        // given
        Long userId = 1L;

        doNothing().when(adminService).deleteUserByAdmin(userId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/admin/users/{userId}", userId)
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("회원 삭제 성공."));
    }

    @Test
    @DisplayName("등록 처리 중이 가게 불러오기 테스트")
    void readAllPendingRestaurantTest() throws Exception {
        // given
        Page<RestaurantManageDto> restaurants = new PageImpl<>(List.of(
                new RestaurantManageDto(restaurant1),
                new RestaurantManageDto(restaurant2)
        ));

        given(adminRestaurantService.readPendingRestaurant(any(Pageable.class))).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/pending-restaurants")
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("가게1"))
                .andExpect(jsonPath("$.content[1].name").value("가게2"));
    }

    @Test
    @DisplayName("등록 처리 중 특정 가게 불러오기 테스트")
    void readPendingRestaurantTest() throws Exception {
        // given
        PendingRestaurantDetailsDto pendingRestaurantDto = new PendingRestaurantDetailsDto(restaurant1);

        given(adminRestaurantService.readPendingDetailsInfo(restaurant1.getId())).willReturn(pendingRestaurantDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/pending-restaurants/{restaurantId}", restaurant1.getId())
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("가게1"));
    }

    @Test
    @DisplayName("등록된 가게 전체 불러오기 테스트")
    void readAllApprovedRestaurantTest() throws Exception {
        // given
        Page<RestaurantManageDto> restaurants = new PageImpl<>(List.of(
                new RestaurantManageDto(restaurant3),
                new RestaurantManageDto(restaurant4)
        ));

        given(adminRestaurantService.readApprovedRestaurant(any(Pageable.class))).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/approved-restaurants")
                        .with(user(principalDetails))
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "id")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("테스트가게1"))
                .andExpect(jsonPath("$.content[0].ownerName").value(test2.getName()))
                .andExpect(jsonPath("$.content[1].name").value("테스트가게2"))
                .andExpect(jsonPath("$.content[1].ownerName").value(test2.getName()));

    }

    @Test
    @DisplayName("등록된 가게 가게 이름으로 불러오기 테스트")
    void readApprovedRestaurantByRestaurantNameTest() throws Exception {
        // given
        Page<RestaurantManageDto> restaurants = new PageImpl<>(List.of(
                new RestaurantManageDto(restaurant3),
                new RestaurantManageDto(restaurant4)
        ));

        given(adminRestaurantService.readApprovedRestaurantByRestaurantName(anyString(), any(Pageable.class))).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/approved-restaurants")
                        .with(user(principalDetails))
                        .param("type", "restaurant")
                        .param("search-keyword", "테스트")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "id")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("테스트가게1"))
                .andExpect(jsonPath("$.content[1].name").value("테스트가게2"));
    }

    @Test
    @DisplayName("등록된 가게 사장 이름으로 불러오기 테스트")
    void readApprovedRestaurantByOwnerNameTest() throws Exception {
        // given
        Page<RestaurantManageDto> restaurants = new PageImpl<>(List.of(
                new RestaurantManageDto(restaurant1),
                new RestaurantManageDto(restaurant2)
        ));

        given(adminRestaurantService.readApprovedRestaurantByOwnerName(anyString(), any(Pageable.class))).willReturn(restaurants);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/approved-restaurants")
                        .with(user(principalDetails))
                        .param("type", "owner")
                        .param("search-keyword", "user1")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "id")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].ownerName").value(test1.getName()))
                .andExpect(jsonPath("$.content[1].ownerName").value(test1.getName()));
    }

    @Test
    @DisplayName("등록된 가게 불러오기 실패 테스트 - 검색 유형 불일치")
    void readApprovedRestaurantFailedInvalidTest() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/admin/approved-restaurants")
                        .with(user(principalDetails))
                        .param("type", "no-exists")
                        .param("search-keyword", ".")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort-by", "id")
                        .param("direction", "ASC")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()));

    }

    @Test
    @DisplayName("가게 추가 요청 승인 테스트")
    void approvalRestaurantTest() throws Exception {
        // given
        Long restaurantId = 1L;

        doNothing().when(adminRestaurantService).approvalRestaurant(restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/admin/restaurants/{restaurantId}", restaurantId)
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("사용자 가게 등록 완료."));
    }

    @Test
    @DisplayName("Admin 가게 삭제 테스트")
    void deleteRestaurantByAdminTest() throws Exception {
        // given
        Long restaurantId = 1L;

        doNothing().when(adminRestaurantService).deleteRestaurant(restaurantId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/admin/restaurants/{restaurantId}", restaurantId)
                        .with(user(principalDetails))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("가게 삭제 완료."));
    }
}
