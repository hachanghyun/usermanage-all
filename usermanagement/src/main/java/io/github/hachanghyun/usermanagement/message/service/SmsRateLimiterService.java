package io.github.hachanghyun.usermanagement.message.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SmsRateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_PER_MINUTE = 500;
    private static final Duration TTL = Duration.ofMinutes(1);

    public boolean tryAcquire(String key) {
        String redisKey = "sms-limit:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count == 1) {
            redisTemplate.expire(redisKey, TTL);
        }

        return count <= MAX_PER_MINUTE;
    }
}
