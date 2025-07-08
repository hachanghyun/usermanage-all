package io.github.hachanghyun.usermanagement.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String account;
    private String password;
}
