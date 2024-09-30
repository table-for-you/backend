package com.project.tableforyou.domain.user.repository;

import com.project.tableforyou.domain.user.entity.Role;
import com.project.tableforyou.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndEmail(String username, String email);
    Page<User> findByNicknameContaining(String searchKeyword, Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);


    @Transactional
    @Modifying
    @Query("update User u set u.loginAttempt = u.loginAttempt + 1 where u.username = :username")
    void updateLoginAttempt(@Param("username") String username);

    @Query("SELECT u.fcmToken FROM User u WHERE u.username = :username")
    String findFcmTokenByUsername(@Param("username") String username);

    @Query("SELECT u.fcmToken FROM Restaurant r JOIN r.user u WHERE r.id = :restaurantId")
    String findFcmTokenByRestaurantId(@Param("restaurantId") Long restaurantId);

}
