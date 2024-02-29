package com.project.tableforyou.repository;

import com.project.tableforyou.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);
    boolean existsByUsername(String username);
}
