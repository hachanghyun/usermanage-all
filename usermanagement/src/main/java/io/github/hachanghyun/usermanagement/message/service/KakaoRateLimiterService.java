package io.github.hachanghyun.usermanagement.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class KakaoRateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_PER_MINUTE = 100;
    private static final Duration TTL = Duration.ofMinutes(5);

    public boolean tryAcquire(String key) {
        String redisKey = "kakao-limit:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count == 1) {
            redisTemplate.expire(redisKey, TTL);
        }

        return count <= MAX_PER_MINUTE;
    }
}
