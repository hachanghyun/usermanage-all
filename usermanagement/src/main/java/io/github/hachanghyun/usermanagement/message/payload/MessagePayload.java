package io.github.hachanghyun.usermanagement.message.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagePayload {
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("message")
    private String message;
    @JsonProperty("name")
    private String name;

}

