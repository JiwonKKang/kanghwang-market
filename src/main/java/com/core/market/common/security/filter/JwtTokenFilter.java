package com.core.market.common.security.filter;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
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

        String refreshToken = jwtTokenUtil.extractRefreshToken(request)
                .orElse(null);

        if (refreshToken != null) {
            RefreshToken token = tokenCacheRepository.getRefreshToken(refreshToken)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN, "리프레시 토큰이 유효하지 않습니다."));

            sendAccessTokenAndRefreshToken(token, response);
            filterChain.doFilter(request, response);
            return;
        }

        jwtTokenUtil.extractAccessToken(request)
                .flatMap(jwtTokenUtil::extractEmail)
                .map(memberService::findByEmail)
                .ifPresent(this::saveAuthentication);

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Member member) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                member, null,
                member.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("{} JWT 인증 성공", member.getUsername());
    }

    private void sendAccessTokenAndRefreshToken(RefreshToken token, HttpServletResponse response) {
        HashMap<String, Object> claims = new HashMap<>();

        List<String> roles = customAuthorityUtils.getAuthoritiesAsString(token.getEmail());

        claims.put("email", token.getEmail());
        claims.put("roles", roles);

        String reIssuedRefreshToken = reIssuedRefreshToken(token);
        String reIssuedAccessToken = jwtTokenizer.generateAccessToken(claims, token.getEmail(), jwtTokenizer.getTokenExpiration());

        jwtTokenUtil.sendAccessAndRefreshToken(response, reIssuedAccessToken, reIssuedRefreshToken);
        log.info("액세스 토큰 및 리프레시 토큰 재발급 완료 - {}", token.getEmail());
    }

    private String reIssuedRefreshToken(RefreshToken refreshToken) {
        tokenCacheRepository.deleteRefreshToken(refreshToken.getRefreshToken());
        String reIssuedRefreshToken = jwtTokenizer.generateRefreshToken();
        
        tokenCacheRepository.setRefreshToken(RefreshToken.of(refreshToken.getEmail(), reIssuedRefreshToken));
        return reIssuedRefreshToken;
    }


}
