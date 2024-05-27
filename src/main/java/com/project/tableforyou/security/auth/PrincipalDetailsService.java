package com.project.tableforyou.security.auth;

import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "#username")
    public PrincipalDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));
        return new PrincipalDetails(user);
    }

    /* 회원의 정보가 업데이트 된다면, 캐싱된 정보도 업데이트 */
    @CachePut(value = "users", key = "#user.username")
    public PrincipalDetails updateUserCache(User user) {
        return new PrincipalDetails(user);
    }
}
