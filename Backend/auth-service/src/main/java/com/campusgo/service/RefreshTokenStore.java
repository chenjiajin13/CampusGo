package com.campusgo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class RefreshTokenStore {

    private final StringRedisTemplate redis;

    public RefreshTokenStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /**
     * Key：refreshToken -> userId
     */
    private String key(String refreshToken) {
        return "campusgo:auth:rt:" + refreshToken;
    }


    public void save(String refreshToken, Long userId, long expiresAt) {
        long now = Instant.now().getEpochSecond();
        long ttlSeconds = expiresAt - now;


        if (ttlSeconds <= 0) {

            return;
        }

        redis.opsForValue().set(
                key(refreshToken),
                String.valueOf(userId),
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }

    public Long verifyAndGetUser(String refreshToken) {
        String v = redis.opsForValue().get(key(refreshToken));
        if (v == null) return null;

        try {
            return Long.valueOf(v);
        } catch (NumberFormatException ex) {

            return null;
        }
    }

    public void revoke(String refreshToken) {
        redis.delete(key(refreshToken));
    }
}