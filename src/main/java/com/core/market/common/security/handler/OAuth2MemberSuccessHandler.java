package com.core.market.common.security.handler;

import com.core.market.common.util.JwtTokenizer;
import com.core.market.user.cache.MemberCacheRepository;
import com.core.market.user.cache.RefreshToken;
import com.core.market.user.app.MemberService;
import com.core.market.user.domain.Member;
import com.core.market.user.domain.OAuth2CustomUser;
import com.core.market.user.cache.TokenCacheRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
    private final TokenCacheRepository tokenCacheRepository;
    private final MemberCacheRepository memberCacheRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess 진입 - 소셜로그인 성공 로직");
        OAuth2CustomUser oAuth2User = (OAuth2CustomUser) authentication.getPrincipal();

        List<String> roles = oAuth2User.getAuthorities().stream()
                .map(String::valueOf)
                .toList(); // Authorities에서 문자열로 권한 추출

        log.info("roles - {}", roles);

        String email = oAuth2User.getEmail();
        redirect(request, response, email, roles);

    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String email, List<String> roles) throws IOException {
        String accessToken = delegateAccessToken(email, roles);  // Access Token 생성// Refresh Token 생성
        String refreshToken = jwtTokenizer.generateRefreshToken(email);
        Member member = memberService.findByEmail(email);

        tokenCacheRepository.getRefreshToken(email) // 이미 로그인 한 유저가 또 로그인했을 경우 리프레시토큰 갱신
                        .ifPresent(token ->
                                tokenCacheRepository.deleteRefreshToken(token.getEmail())
                        );

        tokenCacheRepository.setRefreshToken(RefreshToken.of(member.getEmail(), refreshToken));
        memberCacheRepository.setMember(member); // 로그인 시 유저 캐싱

        log.info("로그인 성공 accessToken 발급  - {}", accessToken);
        log.info("로그인 성공 refreshToken 발급 - {}", refreshToken);

        if (roles.contains("ROLE_GUEST")) { // 첫 소셜로그인 하는 유저일경우 추가 회원정보를 입력하는 폼으로 리다이렉트
            response.sendRedirect(createSignURI(accessToken, refreshToken));
            return;
        }
        response.sendRedirect(createMainURI(accessToken, refreshToken));// 메인화면쪽으로 리다이렉트
    }

    // Access Token 생성
    private String delegateAccessToken(String email, List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles", authorities);

        return jwtTokenizer.generateAccessToken(claims, email, jwtTokenizer.getTokenExpiration());
    }

    private String createMainURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("accessToken", "Bearer " + accessToken);
        queryParam.add("refreshToken", "Bearer " + refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .port(3000)
                .queryParams(queryParam)
                .build()
                .toUri()
                .toString();
    }

    private String createSignURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("accessToken", "Bearer " + accessToken);
        queryParam.add("refreshToken", "Bearer " + refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .port(3000)
                .path("/sign-up")
                .queryParams(queryParam)
                .build()
                .toUri()
                .toString();
    }
}
