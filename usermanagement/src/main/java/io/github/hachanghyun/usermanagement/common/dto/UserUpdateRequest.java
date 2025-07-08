package io.github.hachanghyun.usermanagement.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @Schema(description = "변경할 비밀번호", example = "5678")
    private String password;

    @Schema(description = "변경할 주소", example = "서울시 송파구")
    private String address;
}
