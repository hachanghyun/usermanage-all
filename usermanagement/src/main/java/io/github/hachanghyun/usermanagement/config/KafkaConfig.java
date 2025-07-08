package io.github.hachanghyun.usermanagement.config;

import io.github.hachanghyun.usermanagement.message.payload.MessagePayload;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // ======= Producer =======

    @Bean
    public ProducerFactory<String, MessagePayload> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MessagePayload> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ======= Consumer Factory 공통 =======

    private ConsumerFactory<String, MessagePayload> createConsumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(MessagePayload.class, false));
    }

    // ======= ListenerContainerFactory - 20대 =======

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessagePayload> kafkaListenerContainerFactory20s() {
        ConcurrentKafkaListenerContainerFactory<String, MessagePayload> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory("message-group-20s"));
        return factory;
    }

    // ======= ListenerContainerFactory - 30대 =======

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessagePayload> kafkaListenerContainerFactory30s() {
        ConcurrentKafkaListenerContainerFactory<String, MessagePayload> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory("message-group-30s"));
        return factory;
    }
}
