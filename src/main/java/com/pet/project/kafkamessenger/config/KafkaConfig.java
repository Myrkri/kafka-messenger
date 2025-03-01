package com.pet.project.kafkamessenger.config;

import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ContainerGroupSequencer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaConsumer<String, MessageMetadataDTO> consumer() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9094");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.appsdeveloperblog.ws.core");
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, "product-created-events");
        return new KafkaConsumer<>(config, new StringDeserializer(), new JsonDeserializer<>(MessageMetadataDTO.class));
    }

    @Bean
    public ContainerGroupSequencer sequencer(KafkaListenerEndpointRegistry registry) {
        return new ContainerGroupSequencer(registry, 5000, "validators", "notifications");
    }

    @Bean
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
        return new KafkaListenerEndpointRegistry();
    }
}
