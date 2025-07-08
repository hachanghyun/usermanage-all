package io.github.hachanghyun.usermanagement.register.controller;

import io.github.hachanghyun.usermanagement.common.dto.UserSignupRequest;
import io.github.hachanghyun.usermanagement.register.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequest req) {
        userService.signup(req);
        return ResponseEntity.ok("회원가입 완료");
    }
}
