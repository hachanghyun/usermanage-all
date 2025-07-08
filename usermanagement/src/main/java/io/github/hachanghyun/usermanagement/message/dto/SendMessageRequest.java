package io.github.hachanghyun.usermanagement.message.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    private String ageGroup;
    private String message;
}
