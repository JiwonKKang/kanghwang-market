package com.core.market.user.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TokenCacheRepository{

    private final RedisTemplate<String, RefreshToken> tokenRedisTemplate;
    private static final Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setRefreshToken(RefreshToken refreshToken) {
        String key = getKey(refreshToken.getAccessToken());
        log.info("Set Refresh Token from {} : {}", key, refreshToken);
        tokenRedisTemplate.opsForValue().setIfAbsent(key, refreshToken, USER_CACHE_TTL);
    }

    public Optional<RefreshToken> getRefreshToken(String accessToken) {
        String key = getKey(accessToken);
        RefreshToken token = tokenRedisTemplate.opsForValue().get(key);
        log.info("Get Refresh Token from {} : {}", key, token);
        if (token == null) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

    public void deleteRefreshToken(String accessToken) {
        String key = getKey(accessToken);
        tokenRedisTemplate.delete(key);
        log.info("리프레시 토큰 폐기 완료 - {}", key);
    }

    public String getKey(String accessToken) {
        return "refreshToken:" + accessToken;
    }

}
