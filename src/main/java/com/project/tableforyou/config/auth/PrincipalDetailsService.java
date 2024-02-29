package com.project.tableforyou.config.auth;

import com.project.tableforyou.domain.entity.User;
import com.project.tableforyou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null || username.equals("")) {
            throw new UsernameNotFoundException(username);
        }

        User userEntity = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. username: " + username));


        if(userEntity == null) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        return new PrincipalDetails(userEntity);
    }
}
