package io.github.hachanghyun.usermanagement.user.controller;

import io.github.hachanghyun.usermanagement.common.dto.UserInfoResponse;
import io.github.hachanghyun.usermanagement.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserInfoController {

    @GetMapping("/me")
    public UserInfoResponse getMyInfo(@AuthenticationPrincipal User user) {
        return UserInfoResponse.builder()
                .account(user.getAccount())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .topLevelAddress(extractTopLevel(user.getAddress()))
                .build();
    }

    private String extractTopLevel(String address) {
        if (address == null || address.isBlank()) return "";
        String[] parts = address.trim().split(" ");
        return parts[0];
    }
}