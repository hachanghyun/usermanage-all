package io.github.hachanghyun.usermanagement.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoRequest {
    private String phone;
    private String message;
}
