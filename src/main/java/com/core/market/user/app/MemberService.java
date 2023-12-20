package com.core.market.user.app;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.user.api.request.MemberCreateRequest;
import com.core.market.user.cache.MemberCacheRepository;
import com.core.market.user.domain.Coordinate;
import com.core.market.user.domain.Member;
import com.core.market.user.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberCacheRepository memberCacheRepository;

    public Member findByEmail(String email) {
        return memberCacheRepository.getMember(email).orElseGet(() ->
                memberRepository.findByEmail(email).orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)));
    }

    @Transactional
    public void createMember(MemberCreateRequest request, String email) {
        Member member = findByEmail(email);
        Point point = coordinateToPoint(request.coordinate());

        member.update(request.address(), request.searchScope(), point);
    }

    private Point coordinateToPoint(Coordinate coordinate) { 
        String pointWKT = String.format("POINT(%f %f)", coordinate.lat(), coordinate.lng());
        try {
            return (Point) new WKTReader().read(pointWKT);
        } catch (ParseException e) {
            log.warn("좌표변환중 오류 발생");
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
    }
}
