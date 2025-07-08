package io.github.hachanghyun.usermanagement.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequest {

    @Schema(description = "계정 ID", example = "test123")
    private String account;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "주민등록번호", example = "9001011234567")
    private String residentRegistrationNumber;

    @Schema(description = "휴대폰 번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "주소", example = "서울시 강남구")
    private String address;
}
