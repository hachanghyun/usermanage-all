package io.github.hachanghyun.usermanagement.login.controller;

import io.github.hachanghyun.usermanagement.common.dto.*;
import io.github.hachanghyun.usermanagement.auth.jwt.JwtTokenProvider;
import io.github.hachanghyun.usermanagement.user.entity.User;
import io.github.hachanghyun.usermanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("비밀번호가 일치하지 않습니다."));
        }

        String token = jwtTokenProvider.createToken(user.getAccount());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}