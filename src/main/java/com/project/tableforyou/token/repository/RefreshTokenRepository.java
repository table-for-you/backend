package com.project.tableforyou.token.repository;

import com.project.tableforyou.token.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
