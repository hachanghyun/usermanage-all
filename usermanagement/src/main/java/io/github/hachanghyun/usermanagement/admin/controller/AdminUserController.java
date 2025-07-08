package io.github.hachanghyun.usermanagement.admin.controller;

import io.github.hachanghyun.usermanagement.common.dto.*;
import io.github.hachanghyun.usermanagement.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public Page<UserDto> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        return adminUserService.getAllUsers(pageable);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return adminUserService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
    }
}
