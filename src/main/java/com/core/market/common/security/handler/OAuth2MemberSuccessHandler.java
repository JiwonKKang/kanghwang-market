package com.core.market.common.security.handler;

import com.core.market.common.util.JwtTokenizer;
import com.core.market.common.util.CustomAuthorityUtils;
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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CustomAuthorityUtils authorityUtils;
    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
    private final TokenCacheRepository tokenCacheRepository;
    private final MemberCacheRepository memberCacheRepository;

    private final static String FRONT_SIGN_UP_REDIRECT_URI = "http://localhost:3000/oauth";
    private final static String FRONT_REDIRECT_URI = "http://localhost:3000";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess 진입 - 소셜로그인 성공 로직");
        OAuth2CustomUser oAuth2User = (OAuth2CustomUser) authentication.getPrincipal();

        List<String> roles = oAuth2User.getAuthorities().stream()
                .map(String::valueOf)
                .toList(); // Authorities에서 문자열로 권한 추출

        log.info("roles - {}", roles);

        String email = oAuth2User.getEmail();
        redirect(response, email, roles);

    }

    private void redirect(HttpServletResponse response, String email, List<String> roles) throws IOException {
        String accessToken = delegateAccessToken(email, roles);  // Access Token 생성// Refresh Token 생성
        String refreshToken = jwtTokenizer.generateRefreshToken();
        Member member = memberService.findByEmail(email);
        tokenCacheRepository.setRefreshToken(RefreshToken.of(member.getEmail(), refreshToken));
        memberCacheRepository.setMember(member);

        response.addHeader("access-token", "Bearer " + accessToken);// Access Token과 Refresh Token을 포함한 URL을 생성
        response.addHeader("refresh-token", "Bearer " + refreshToken);
        response.setContentType("application/json;charset=UTF-8");

        if (roles.contains("ROLE_GUEST")) { // 첫 소셜로그읺 하는 유저일경우 추가 회원정보를 입력하는 폼으로 리다이렉트
            response.sendRedirect(FRONT_SIGN_UP_REDIRECT_URI);
            return;
        }
        response.sendRedirect(FRONT_REDIRECT_URI);// 메인화면쪽으로 리다이렉트
    }

    // Access Token 생성
    private String delegateAccessToken(String email, List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles", authorities);

        return jwtTokenizer.generateAccessToken(claims, email, jwtTokenizer.getTokenExpiration());
    }

}
