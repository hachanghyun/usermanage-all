package io.github.hachanghyun.usermanagement.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/redis")
@Profile("!prod") // 운영에서는 절대 노출 안 되도록
public class RedisDebugController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/keys")
    public Map<String, String> getAllKeys(@RequestParam(required = false) String pattern) {
        Set<String> keys = redisTemplate.keys(pattern != null ? pattern : "*");
        Map<String, String> result = new HashMap<>();
        if (keys != null) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                result.put(key, value);
            }
        }
        return result;
    }
}
