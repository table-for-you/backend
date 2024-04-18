package com.project.tableforyou.token.repository;

import com.project.tableforyou.token.entity.AccessToken;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRepository extends CrudRepository<AccessToken, String> {
}
