package com.core.market.user.app;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.user.cache.MemberCacheRepository;
import com.core.market.user.domain.Member;
import com.core.market.user.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void saveUser(Member member) {
        memberRepository.save(member);
    }
}
