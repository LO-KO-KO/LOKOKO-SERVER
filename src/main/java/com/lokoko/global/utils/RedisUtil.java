package com.lokoko.global.utils;

import com.lokoko.global.auth.jwt.exception.RefreshTokenNotFoundException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final static String TOKEN_FORMAT = "refreshToken:%s";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${lokoko.jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    public void setRefreshToken(Long id, String value) {
        String key = String.format(TOKEN_FORMAT, id);
        redisTemplate.opsForValue().set(key, value, refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public Object getRefreshToken(Long id) {
        String key = String.format(TOKEN_FORMAT, id);
        Object getObjecet = redisTemplate.opsForValue().get(key);

        if (getObjecet == null) {
            throw new RefreshTokenNotFoundException();
        }

        return getObjecet;
    }
}
