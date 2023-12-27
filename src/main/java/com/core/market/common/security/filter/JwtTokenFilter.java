package com.core.market.common.security.filter;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.common.util.JwtTokenUtil;
import com.core.market.common.util.JwtTokenizer;
import com.core.market.user.app.MemberService;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenCacheRepository tokenCacheRepository;
    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("-------------- 유저 검증 필터 진입 ---------------");
        String refreshToken = jwtTokenUtil.extractRefreshToken(request)
                .orElse(null);

        if (refreshToken != null) {
            log.info("리프레시 토큰이 헤더에 존재 - {}", refreshToken);

            String email = jwtTokenUtil.extractEmailFromRefreshToken(refreshToken);

            RefreshToken validRefreshToken = tokenCacheRepository.getRefreshToken(email)
                    .filter(existingToken -> existingToken.getRefreshToken().equals(refreshToken))
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

            log.info("리프레스 토큰 유효, 재발급 로직 실행");
            sendAccessTokenAndRefreshToken(validRefreshToken.getEmail(), response);
            filterChain.doFilter(request,response);
            return;
        }


        jwtTokenUtil.extractAccessToken(request)
                .map(jwtTokenUtil::extractEmail)
                .filter(memberService::isLoginUser)
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
        log.info("{} 유저 인증 성공", member.getUsername());
    }

    private void sendAccessTokenAndRefreshToken(String email, HttpServletResponse response){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        String reIssuedAccessToken = jwtTokenizer.generateAccessToken(claims, email, jwtTokenizer.getTokenExpiration());
        String reIssuedRefreshToken = reIssuedRefreshToken(email);

        jwtTokenUtil.sendAccessAndRefreshToken(response, reIssuedAccessToken, reIssuedRefreshToken);
        log.info("액세스 토큰 및 리프레시 토큰 재발급 완료 - {}", email);
        log.info("재발급 액세스 토큰 - {}", reIssuedAccessToken);
        log.info("재발급 리프레시 토큰 - {}", reIssuedRefreshToken);
    }

    private String reIssuedRefreshToken(String email) {
        tokenCacheRepository.deleteRefreshToken(email);
        String reIssuedRefreshToken = jwtTokenizer.generateRefreshToken(email);
        
        tokenCacheRepository.setRefreshToken(RefreshToken.of(email, reIssuedRefreshToken));
        return reIssuedRefreshToken;
    }


}
