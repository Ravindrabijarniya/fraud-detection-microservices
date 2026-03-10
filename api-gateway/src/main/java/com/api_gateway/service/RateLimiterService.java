package com.api_gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Boolean> isAllowed(String rawKey, int limit, Duration window) {

        String key = "rate-limit:" + rawKey;

        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        // first hit → set expiry
                        return redisTemplate.expire(key, window)
                                .thenReturn(true);
                    }
                    return Mono.just(count <= limit);
                });
    }
}
