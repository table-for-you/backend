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
    @Cacheable(value = "users", key = "#userId")
    public PrincipalDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() ->
                new UsernameNotFoundException("User not found with userId: " + userId));
        return new PrincipalDetails(user);
    }
}
