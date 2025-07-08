package io.github.hachanghyun.usermanagement.test;

import io.github.hachanghyun.usermanagement.user.entity.User;
import io.github.hachanghyun.usermanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Profile("generate-users") // 실행할 때만 이 프로필 활성화
public class DummyUserGenerator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println("이미 유저가 존재함. 생성 생략.");
            return;
        }

        // 700명: 20대
        IntStream.rangeClosed(1, 700).forEach(i -> {
            userRepository.save(generateUser(i, "000101", "3")); // 2000년생, 남
        });

        // 700명: 30대
        IntStream.rangeClosed(701, 1400).forEach(i -> {
            userRepository.save(generateUser(i, "900101", "1")); // 1990년생, 남
        });

        // 1600명: 기타 연령대 랜덤
        IntStream.rangeClosed(1401, 3000).forEach(i -> {
            String birth;
            String gender = i % 2 == 0 ? "1" : "2";

            switch (i % 5) {
                case 0: birth = "101010"; break; // 2010년생 → 10대
                case 1: birth = "800101"; break; // 1980년생 → 40대
                case 2: birth = "700101"; break; // 1970년생 → 50대
                case 3: birth = "600101"; break; // 1960년생 → 60대
                default: birth = "500101"; break; // 1950년생 → 70대
            }

            userRepository.save(generateUser(i, birth, gender));
        });

        System.out.println("✅ 3000명 더미 유저 생성 완료");
    }

    private User generateUser(int i, String birth, String gender) {
        String rrn = birth + "-" + gender + String.format("%06d", i);
        String phone = String.format("010%08d", i);

        return User.builder()
                .account("user" + i)
                .password("pass" + i)
                .name("테스트" + i)
                .residentRegistrationNumber(rrn)
                .phoneNumber(phone)
                .address("서울시 강남구 " + i)
                .build();
    }
}
