package io.github.hachanghyun.usermanagement.user.repository;

import io.github.hachanghyun.usermanagement.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAccount(String account); // ✅ 이거 추가
    boolean existsByAccount(String account);
    boolean existsByResidentRegistrationNumber(String residentRegistrationNumber);
}
