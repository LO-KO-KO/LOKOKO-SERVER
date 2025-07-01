package com.lokoko.global.utils;

import com.lokoko.global.auth.jwt.exception.RefreshTokenNotFoundException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public void setRefreshToken(String redisKey, String refreshToken, long expireMs) {
        redisTemplate.opsForValue().set(redisKey, refreshToken, expireMs, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String key) {
        String token = redisTemplate.opsForValue().get(key);
        if (token == null) {
            throw new RefreshTokenNotFoundException();
        }
        return token;
    }

    public void deleteRefreshToken(String redisKey) {
        redisTemplate.delete(redisKey);
    }
}
