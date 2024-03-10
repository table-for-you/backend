package com.project.tableforyou.repository;

import com.project.tableforyou.domain.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthRepository extends JpaRepository<Auth, UUID> {

    Auth findByUsername(String username);

    boolean existsByUsername(String username);
}