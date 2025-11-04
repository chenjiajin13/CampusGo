package com.campusgo.service;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RefreshTokenStore {

    // refreshToken -> (userId, expiresAt)
    private final Map<String, RefreshEntry> store = new ConcurrentHashMap<>();

    public void save(String refreshToken, Long userId, long expiresAt) {
        store.put(refreshToken, new RefreshEntry(userId, expiresAt));
    }

    public Long verifyAndGetUser(String refreshToken) {
        RefreshEntry e = store.get(refreshToken);
        if (e == null) return null;

        if (e.expiresAt < Instant.now().getEpochSecond()) {
            store.remove(refreshToken);
            return null;
        }
        return e.userId;
    }

    public void revoke(String refreshToken) {
        store.remove(refreshToken);
    }


    private static final class RefreshEntry {
        final Long userId;
        final long expiresAt;

        RefreshEntry(Long userId, long expiresAt) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }
}

