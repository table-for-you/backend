package com.project.tableforyou.security.oauth;

import com.project.tableforyou.domain.user.dto.UserRequestDto;
import com.project.tableforyou.domain.user.entity.User;
import com.project.tableforyou.domain.user.repository.UserRepository;
import com.project.tableforyou.security.auth.PrincipalDetails;
import com.project.tableforyou.security.oauth.provider.FacebookUserInfo;
import com.project.tableforyou.security.oauth.provider.GoogleUserInfo;
import com.project.tableforyou.security.oauth.provider.NaverUserInfo;
import com.project.tableforyou.security.oauth.provider.OAuth2UserInfo;
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
        // 부모 클래스의 loadUser 메서드를 호출하여 OAuth2 인증된 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth2UserInfo 인터페이스 구현체
        OAuth2UserInfo oAuth2UserInfo = null;

        // 클라이언트 등록 ID 가져오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 클라이언트 등록 ID에 따라 적절한 OAuth2UserInfo 객체 생성
        switch (registrationId) {
            case "facebook" -> oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
            case "google" -> oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            case "naver" -> oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }

        // OAuth2UserInfo를 기반으로 사용자 엔티티 생성 또는 업데이트
        User userEntity = saveOrUpdate(oAuth2UserInfo);

        // PrincipalDetails 객체를 사용자 엔티티와 OAuth2 사용자의 속성으로 생성하여 반환
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }


    private User saveOrUpdate(OAuth2UserInfo oAuth2UserInfo) {

        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                .map(User::updateModifiedDateIfUserExists)  // 이미 있다면 사용자 접근 시간 업데이트
                .orElse(new UserRequestDto().toEntity(oAuth2UserInfo, bCryptPasswordEncoder)); // 없다면 새로 만들기.
        userRepository.save(user);

        return user;
    }
}
