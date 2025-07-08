package io.github.hachanghyun.usermanagement.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private String account;
    private String name;
    private String phoneNumber;
    private String topLevelAddress;
}