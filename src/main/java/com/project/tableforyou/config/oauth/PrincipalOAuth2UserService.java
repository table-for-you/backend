package com.project.tableforyou.config.oauth;

import com.project.tableforyou.config.auth.PrincipalDetails;
import com.project.tableforyou.config.oauth.provider.FacebookUserInfo;
import com.project.tableforyou.config.oauth.provider.GoogleUserInfo;
import com.project.tableforyou.config.oauth.provider.NaverUserInfo;
import com.project.tableforyou.config.oauth.provider.OAuth2UserInfo;
import com.project.tableforyou.domain.dto.UserDto;
import com.project.tableforyou.domain.entity.User;
import com.project.tableforyou.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("ClientRegistration: " + userRequest.getClientRegistration());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        switch (registrationId) {
            case "facebook" -> oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
            case "google" -> oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            case "naver" -> oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }
        User userEntity = saveOrUpdate(oAuth2UserInfo);

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }


    private User saveOrUpdate(OAuth2UserInfo oAuth2UserInfo) {
        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                .map(User::updateModifiedDateIfUserExists)
                .orElse(new UserDto.Request().toEntity(oAuth2UserInfo, bCryptPasswordEncoder));
        userRepository.save(user);

        return user;
    }
}
