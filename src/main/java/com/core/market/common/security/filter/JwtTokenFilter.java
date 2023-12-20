package com.core.market.common.security.filter;

import com.core.market.common.util.CustomAuthorityUtils;
import com.core.market.common.util.JwtTokenUtil;
import com.core.market.common.util.JwtTokenizer;
import com.core.market.user.app.MemberService;
import com.core.market.user.cache.MemberCacheRepository;
import com.core.market.user.cache.RefreshToken;
import com.core.market.user.cache.TokenCacheRepository;
import com.core.market.user.domain.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenCacheRepository tokenCacheRepository;
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT 토큰 필터 진입");

        String refreshToken = jwtTokenUtil.extractRefreshToken(request)
                .orElse(null);

        if (refreshToken != null) {
            tokenCacheRepository.getRefreshToken(refreshToken)
                    .ifPresent(token -> {
                        sendAccessTokenAndRefreshToken(token.getEmail(), response);
                    });
            filterChain.doFilter(request, response);
            return;
        }


        jwtTokenUtil.extractAccessToken(request)
                .flatMap(jwtTokenUtil::extractEmail)
                .map(memberService::findByEmail)
                .ifPresent(this::saveAuthentication);

        log.info("JWT 토큰 필터 종료");

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Member member) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                member, null,
                member.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendAccessTokenAndRefreshToken(String email, HttpServletResponse response) {
        HashMap<String, Object> claims = new HashMap<>();

        List<String> roles = customAuthorityUtils.getAuthoritiesAsString(email);

        log.info("email - {}", email);

        claims.put("email", email);
        claims.put("roles", roles);

        String reIssuedRefreshToken = reIssuedRefreshToken(email);
        String reIssuedAccessToken = jwtTokenizer.generateAccessToken(claims, email, jwtTokenizer.getTokenExpiration());

        jwtTokenUtil.sendAccessAndRefreshToken(response, reIssuedAccessToken, reIssuedRefreshToken);
    }

    private String reIssuedRefreshToken(String email) {
        tokenCacheRepository.deleteRefreshToken(email);
        String reIssuedRefreshToken = jwtTokenizer.generateRefreshToken(email);
        
        tokenCacheRepository.setRefreshToken(RefreshToken.of(email, reIssuedRefreshToken));
        return reIssuedRefreshToken;
    }


}
