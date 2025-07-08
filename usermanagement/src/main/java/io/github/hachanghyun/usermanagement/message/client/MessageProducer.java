package io.github.hachanghyun.usermanagement.message.client;

import io.github.hachanghyun.usermanagement.message.payload.MessagePayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class MessageProducer {

    private final KafkaTemplate<String, MessagePayload> kafkaTemplate;

    public MessageProducer(KafkaTemplate<String, MessagePayload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String phone, String message, String name, int birthYear) {
        MessagePayload payload = new MessagePayload(phone, message, name);
        String topic = resolveTopic(birthYear);

        if (topic == null) {
            throw new IllegalArgumentException("지원하지 않는 연령대: " + (LocalDate.now().getYear() - birthYear));
        }

        log.info("Kafka 전송 → topic: {}, payload: {}", topic, payload);
        CompletableFuture<SendResult<String, MessagePayload>> future = kafkaTemplate.send(topic, payload);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata meta = result.getRecordMetadata();
                log.info("✅ Kafka 전송 성공: payload={}, offset={}, partition={}", payload, meta.offset(), meta.partition());
            } else {
                log.error("❌ Kafka 전송 실패: payload={}, error={}", payload, ex.getMessage());
            }
        });
    }

    private String resolveTopic(int birthYear) {
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - birthYear;

        if (age >= 20 && age < 30) return "message-topic-20s";
        else if (age >= 30 && age < 40) return "message-topic-30s";
        else return null; // 지원하지 않는 연령대는 처리하지 않음
    }
}
