package com.campusgo.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class KafkaDedup {

    private final StringRedisTemplate redis;

    public boolean firstTime(String eventId) {
        Boolean ok = redis.opsForValue().setIfAbsent(
                "kafka:dedup:" + eventId, "1", Duration.ofDays(7));
        return Boolean.TRUE.equals(ok);
    }
}
