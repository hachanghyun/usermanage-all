package io.github.hachanghyun.usermanagement.register.service;

import io.github.hachanghyun.usermanagement.common.dto.UserSignupRequest;
import io.github.hachanghyun.usermanagement.user.entity.User;
import io.github.hachanghyun.usermanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserSignupRequest req) {
        if (userRepository.existsByAccount(req.getAccount())) {
            throw new IllegalArgumentException("이미 존재하는 계정입니다.");
        }
        if (userRepository.existsByResidentRegistrationNumber(req.getResidentRegistrationNumber())) {
            throw new IllegalArgumentException("이미 등록된 주민등록번호입니다.");
        }

        User user = User.builder()
                .account(req.getAccount())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .residentRegistrationNumber(req.getResidentRegistrationNumber())
                .phoneNumber(req.getPhoneNumber())
                .address(req.getAddress())
                .build();

        userRepository.save(user);
    }
}
