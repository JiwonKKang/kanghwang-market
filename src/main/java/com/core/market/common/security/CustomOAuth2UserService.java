package com.core.market.common.security;

import com.core.market.common.util.CustomAuthorityUtils;
import com.core.market.user.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils customAuthorityUtils;


    @Override // 로그인 로직 담당
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService 진입 - 소셜 로그인 로직");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);  // 어떤 플랫폼의 유저정보도 담을수있도록 추상화한 클래스 OAuth2 정보를 가져옵니다.

        Map<String, Object> originAttributes = oAuth2User.getAttributes();  // OAuth2User의 attribute

        // OAuth2 서비스 id (google, kakao, naver)
        // 소셜 정보를 가져옵니다. oauth2/atholization/{kakao} 이부분에 해당
        String registrationId = userRequest.getClientRegistration().getRegistrationId();


        // OAuthAttributes: OAuth2User의 attribute를 서비스 유형에 맞게 담아줄 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, originAttributes);
        Member member = saveOrUpdate(attributes);
        String email = member.getEmail();
        List<GrantedAuthority> authorities = customAuthorityUtils.createAuthorities(email); //Role 정보 가져오기

        return new OAuth2CustomUser(registrationId, originAttributes, authorities, email);
    }

    private Member saveOrUpdate(OAuthAttributes authAttributes) {
        Member member = memberRepository.findByEmail(authAttributes.getEmail())
                .map(entity -> entity.oauthUpdate(authAttributes.getEmail(), authAttributes.getProfileImageUrl()))
                .orElse(authAttributes.toEntity());

        return memberRepository.save(member);
    }
}
