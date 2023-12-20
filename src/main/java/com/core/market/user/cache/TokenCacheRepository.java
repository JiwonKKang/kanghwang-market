package com.core.market.user.cache;

import com.core.market.user.cache.RefreshToken;
import com.core.market.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.naming.factory.SendMailFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TokenCacheRepository{

    @Qualifier("tokenRedisTemplate")
    private final RedisTemplate<String, RefreshToken> tokenRedisTemplate;
    private static final Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setRefreshToken(RefreshToken refreshToken) {
        String key = getKey(refreshToken.getRefreshToken());
        log.info("Set Refresh Token from {} : {}", key, refreshToken);
        tokenRedisTemplate.opsForValue().setIfAbsent(key, refreshToken, USER_CACHE_TTL);
    }

    public Optional<RefreshToken> getRefreshToken(String refreshToken) {
        String key = getKey(refreshToken);
        RefreshToken token = tokenRedisTemplate.opsForValue().get(key);
        log.info("Get Refresh Token from {} : {}", key, token);
        return Optional.ofNullable(token);
    }

    public void deleteRefreshToken(String email) {
        String key = getKey(email);
        tokenRedisTemplate.opsForValue().getAndDelete(key);

    }

    public String getKey(String refreshToken) {
        return "refreshToken:" + refreshToken;
    }

}
