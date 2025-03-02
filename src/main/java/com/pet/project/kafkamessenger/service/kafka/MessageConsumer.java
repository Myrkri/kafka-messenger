package com.pet.project.kafkamessenger.service.kafka;

import com.pet.project.kafkamessenger.dto.MessageDTO;
import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumer {

    private static final Map<String, Integer> MOCK_USER_DB = Map.of("user", 1, "user1", 2, "user2", 3);

    private final KafkaTemplate<String, MessageMetadataDTO> kafkaTemplate;

    @KafkaListener(groupId = "validator", topics = "messages", containerGroup = "validators")
    public void sort(ConsumerRecord<String, MessageDTO> record) {
        MessageDTO message = record.value();
        validateMessage(message);
        log.info("Message received: {}", message);
        final MessageMetadataDTO metadataDTO = populateMetadata(message);
//        kafkaTemplate.send("chat", MOCK_USER_DB.get(metadataDTO.getReceiver()), metadataDTO.getSender(), metadataDTO);
        kafkaTemplate.send("chat", metadataDTO);
        log.info("Message sent to sender: {}", metadataDTO.getSender());
    }

    @KafkaListener(groupId = "notifications", topics = "messages", containerGroup = "notifications")
    public void sendNotification(ConsumerRecord<String, MessageDTO> record) {
        //mocked method
        log.info("Notification sent");
    }

    private static void validateMessage(final MessageDTO message) {
        if (!StringUtils.hasText(message.getSender()) || !StringUtils.hasText(message.getMessage())) {
            throw new IllegalArgumentException("Message is not valid");
        }
    }

    private static MessageMetadataDTO populateMetadata(final MessageDTO message) {
        final MessageMetadataDTO metadataDTO = new MessageMetadataDTO()
                .setTimestamp(LocalDateTime.now());
        metadataDTO.setSender(message.getSender());
        metadataDTO.setMessage(message.getMessage());
        metadataDTO.setReceiver(message.getReceiver());
        log.info("Message metadata populated");
        return metadataDTO;
    }

}
