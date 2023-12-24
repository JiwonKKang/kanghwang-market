package com.core.market.user.app;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.common.util.JwtTokenUtil;
import com.core.market.user.api.request.MemberCreateRequest;
import com.core.market.user.cache.LogoutTokenRepository;
import com.core.market.user.cache.MemberCacheRepository;
import com.core.market.user.cache.TokenCacheRepository;
import com.core.market.user.domain.Coordinate;
import com.core.market.user.domain.Member;
import com.core.market.user.domain.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberCacheRepository memberCacheRepository;
    private final TokenCacheRepository tokenCacheRepository;
    private final LogoutTokenRepository logoutTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public Member findByEmail(String email) {
        return memberCacheRepository.getMember(email).orElseGet(() ->
                memberRepository.findByEmail(email).orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void updateMember(MemberCreateRequest request, Member member) {

        Point point = coordinateToPoint(request.coordinate());

        Member updatedMember = member.update(request.address(), request.searchScope(), point);
        memberRepository.saveAndFlush(updatedMember);
        refreshMember(member);
    }

    @Transactional
    public void logout(HttpServletRequest request, String email) {

        String accessToken = request.getHeader("Access-Token");

        tokenCacheRepository.deleteRefreshToken(accessToken);

        long accessTokenExpirationMillis = jwtTokenUtil.getAccessTokenExpirationMillis(accessToken);
        log.info("accessTokenExpirationMillis - {}", accessTokenExpirationMillis);
        logoutTokenRepository.setLogout(email, accessToken, accessTokenExpirationMillis);

    }

    public Boolean isLoginUser(String userEmail) {
        return logoutTokenRepository.isLoginUser(userEmail);
    }

    private Point coordinateToPoint(Coordinate coordinate) {
        String pointWKT = String.format("POINT(%f %f)", coordinate.lat(), coordinate.lng());
        try {
            return (Point) new WKTReader().read(pointWKT);
        } catch (Exception e) {
            log.warn("좌표변환중 오류 발생");
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
    }

    private void refreshMember(Member member) {
        memberCacheRepository.deleteMember(member.getEmail());
        memberCacheRepository.setMember(member);
    }
}
