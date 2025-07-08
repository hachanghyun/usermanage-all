package io.github.hachanghyun.usermanagement.message.service;

import io.github.hachanghyun.usermanagement.message.client.MessageProducer;
import io.github.hachanghyun.usermanagement.user.entity.User;
import io.github.hachanghyun.usermanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final MessageProducer messageProducer;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendMessagesByAgeGroup(String ageGroupText, String message) {
        int targetAge;
        try {
            targetAge = Integer.parseInt(ageGroupText.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("올바르지 않은 연령대 형식입니다. 예: 20대, 30대");
        }

        List<User> users = userRepository.findAll();
        log.info("전체 유저 수: {}", users.size());
        int currentYear = LocalDate.now().getYear();

        users.stream()
                .filter(user -> {
                    String rrn = user.getResidentRegistrationNumber();
                    if (rrn == null || rrn.length() < 8) return false;

                    char genderCode = rrn.charAt(7);
                    int birthYearPrefix;

                    if (genderCode == '1' || genderCode == '2') {
                        birthYearPrefix = 1900;
                    } else if (genderCode == '3' || genderCode == '4') {
                        birthYearPrefix = 2000;
                    } else {
                        log.warn("⚠️ 잘못된 주민번호 성별코드: {}, rrn={}", genderCode, rrn);
                        return false;
                    }

                    int birthYear = birthYearPrefix + Integer.parseInt(rrn.substring(0, 2));
                    int userAge = currentYear - birthYear;

                    return (userAge / 10) * 10 == targetAge;
                })
                .forEach(user -> {
                    String key = "msg:lock:" + user.getPhoneNumber();
                    Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "1");

                    if (Boolean.TRUE.equals(acquired)) {
                        redisTemplate.expire(key, java.time.Duration.ofMinutes(10));

                        String phone = user.getPhoneNumber();
                        String name = user.getName();
                        String rrn = user.getResidentRegistrationNumber();
                        char genderCode = rrn.charAt(7);
                        int birthYearPrefix = (genderCode == '1' || genderCode == '2') ? 1900 : 2000;
                        int birthYear = birthYearPrefix + Integer.parseInt(rrn.substring(0, 2));

                        log.info("Kafka 전송 준비: phone={}, message={}, name={}, birthYear={}", phone, message, name, birthYear);

                        try {
                            messageProducer.sendMessage(phone, message, name, birthYear);
                        } catch (IllegalArgumentException e) {
                            log.warn("❌ Kafka 전송 제외 대상: {} (이유: {})", phone, e.getMessage());
                        }
                    }
                });
    }
}
