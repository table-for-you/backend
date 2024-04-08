package com.project.tableforyou.domain.user.repository;

import com.project.tableforyou.domain.Role;
import com.project.tableforyou.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(Role role);
    boolean existsByNickname(String nickname);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}