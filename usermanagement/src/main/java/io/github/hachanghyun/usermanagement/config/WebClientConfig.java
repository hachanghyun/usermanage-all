package io.github.hachanghyun.usermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient kakaoClient() {
        return WebClient.builder()
                .baseUrl("http://mock-kakao:8081")
                .defaultHeaders(headers -> {
                    headers.setBasicAuth("autoever", "1234");
                    headers.set("Content-Type", "application/json");
                })
                .build();
    }

    @Bean
    public WebClient smsClient() {
        return WebClient.builder()
                .baseUrl("http://mock-sms:8082")
                .defaultHeaders(headers -> {
                    headers.setBasicAuth("autoever", "5678");
                    headers.set("Content-Type", "application/x-www-form-urlencoded");
                })
                .build();
    }
}