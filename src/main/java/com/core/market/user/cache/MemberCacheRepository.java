package com.core.market.user.cache;

import com.core.market.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MemberCacheRepository {

    private final RedisTemplate<String, Member> redisTemplate;
    private static final Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setMember(Member member) {
        String key = getKey(member.getEmail());
        log.info("Set User from {} : {}", key, member);
        redisTemplate.opsForValue().setIfAbsent(key, member, USER_CACHE_TTL);
    }

    public Optional<Member> getMember(String email) {
        String key = getKey(email);
        Member user = redisTemplate.opsForValue().get(key);
        log.info("Get User from {} : {}", key, user);
        return Optional.ofNullable(user);
    }

    public void deleteMember(String email) {
        String key = getKey(email);
        redisTemplate.delete(key);
        log.info("리프레시 토큰 폐기 완료 - {}", key);
    }

    public String getKey(String userName) {
        return "USER:" + userName;
    }
}
