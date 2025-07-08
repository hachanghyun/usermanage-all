package io.github.hachanghyun.usermanagement.admin.service;

import io.github.hachanghyun.usermanagement.common.dto.*;
import io.github.hachanghyun.usermanagement.user.entity.User;
import io.github.hachanghyun.usermanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toDto);
    }

    public UserDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        return toDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .account(user.getAccount())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .build();
    }
}
