package com.core.market.common.security.filter;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.common.Response;
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
import org.springframework.security.core.Authentication;
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
        try {
            log.info("-------------- 유저 검증 필터 진입 ---------------");
            String refreshToken = jwtTokenUtil.extractRefreshToken(request) /*TODO: 혅재 리프레시 토큰이 유효하지않으면 없는거로
                                                                                      간주하고 진행하지만 예외 던져주는걸로 변경*/
                    .orElse(null);

            if (refreshToken != null) {
                log.info("리프레시 토큰이 헤더에 존재 - {}", refreshToken);
                RefreshToken token = tokenCacheRepository.getRefreshToken(refreshToken)
                        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN, "리프레시 토큰이 유효하지 않습니다."));

                log.info("리프레스 토큰 유효, 재발급 로직 실행");
                sendAccessTokenAndRefreshToken(token, response); /*TODO : 이메일을 키로하니까 같은 리프레스토큰으로 계속 재발급가능함;;;;
                                                                            걍 레디스에 쌓이더라도 토큰을 key로 해야할듯*/
                filterChain.doFilter(request,response);
                return;
            }

            log.info("액세스 토큰 추출 및 인증 정보 저장 로직 진입");
            jwtTokenUtil.extractAccessToken(request)
                    .flatMap(jwtTokenUtil::extractEmail)
                    .map(memberService::findByEmail)
                    .ifPresent(this::saveAuthentication);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }
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

    private void sendAccessTokenAndRefreshToken(RefreshToken token, HttpServletResponse response) throws IOException {
        HashMap<String, Object> claims = new HashMap<>();

        List<String> roles = customAuthorityUtils.getAuthoritiesAsString(token.getEmail());

        claims.put("email", token.getEmail());
        claims.put("roles", roles);

        String reIssuedRefreshToken = reIssuedRefreshToken(token);
        String reIssuedAccessToken = jwtTokenizer.generateAccessToken(claims, token.getEmail(), jwtTokenizer.getTokenExpiration());

        jwtTokenUtil.sendAccessAndRefreshToken(response, reIssuedAccessToken, reIssuedRefreshToken);
        log.info("액세스 토큰 및 리프레시 토큰 재발급 완료 - {}", token.getEmail());
        log.info("재발급 액세스 토큰 - {}", reIssuedAccessToken);
        log.info("재발급 리프레시 토큰 - {}", reIssuedRefreshToken);
    }

    private String reIssuedRefreshToken(RefreshToken refreshToken) {
        tokenCacheRepository.deleteRefreshToken(refreshToken.getRefreshToken());
        String reIssuedRefreshToken = jwtTokenizer.generateRefreshToken();
        
        tokenCacheRepository.setRefreshToken(RefreshToken.of(refreshToken.getEmail(), reIssuedRefreshToken));
        return reIssuedRefreshToken;
    }


}
