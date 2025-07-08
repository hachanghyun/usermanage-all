package io.github.hachanghyun.usermanagement.message.client;

import io.github.hachanghyun.usermanagement.message.dto.KakaoRequest;
import io.github.hachanghyun.usermanagement.message.payload.MessagePayload;
import io.github.hachanghyun.usermanagement.message.service.KakaoRateLimiterService;
import io.github.hachanghyun.usermanagement.message.service.SmsRateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class MessageConsumer30s {

    private final WebClient kakaoClient;
    private final WebClient smsClient;
    private final KakaoRateLimiterService kakaoRateLimiterService;
    private final SmsRateLimiterService smsRateLimiterService;

    @KafkaListener(
            topics = "message-topic-30s",
            groupId = "message-group-30s",
            containerFactory = "kafkaListenerContainerFactory30s")
    public void consume(MessagePayload payload) {
        log.info("[30대] Kafka 수신: {}", payload);

        try {
            String phone = payload.getPhoneNumber();
            String message = payload.getMessage();
            String name = payload.getName();
            String fullMessage = name + "님, 안녕하세요. 현대 오토에버입니다.\n" + message;

            if (kakaoRateLimiterService.tryAcquire("kakao-send")) {
                kakaoClient.post()
                        .uri("/kakaotalk-messages")
                        .bodyValue(new KakaoRequest(phone, fullMessage))
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                                response -> {
                                    log.warn("[30대] 카카오톡 API 오류 - {}", response.statusCode());
                                    return Mono.error(new RuntimeException("카카오톡 전송 실패"));
                                })
                        .toBodilessEntity()
                        .doOnSuccess(r -> log.info("[30대] 카카오톡 전송 성공: {}", phone))
                        .onErrorResume(ex -> {
                            log.warn("[30대] 카카오톡 실패, SMS 전환: {}", phone);
                            return sendSmsFallback(phone, fullMessage)
                                    .then(Mono.empty()); // Mono<ResponseEntity<Void>> 타입에 맞춤
                        })
                        .subscribe();
            } else {
                log.warn("[30대] 카카오톡 Rate Limit 초과 - SMS 전환: {}", phone);
                sendSmsFallback(phone, fullMessage).subscribe();
            }

        } catch (Exception e) {
            log.error("[30대] 메시지 처리 중 예외", e);
        }
    }

    private Mono<Void> sendSmsFallback(String phone, String fullMessage) {
        if (!smsRateLimiterService.tryAcquire("sms-send")) {
            log.warn("[30대] SMS Rate Limit 초과 - 전송 제외: {}", phone);
            return Mono.empty();
        }

        return smsClient.post()
                .uri(uriBuilder -> uriBuilder.path("/sms").queryParam("phone", phone).build())
                .bodyValue("message=" + fullMessage)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> log.info("[30대] SMS 전송 성공: {}", phone))
                .doOnError(e -> log.error("[30대] SMS 전송 실패", e))
                .then();
    }
}
