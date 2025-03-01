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

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumer {

    private final KafkaTemplate<String, MessageMetadataDTO> kafkaTemplate;

    @KafkaListener(groupId = "validator", topics = "messages")
    public void sort(ConsumerRecord<String, MessageDTO> record) {
        MessageDTO message = record.value();
        validateMessage(message);
        log.info("Message received: {}", message);
        final MessageMetadataDTO metadataDTO = populateMetadata(message);
        kafkaTemplate.send(message.getSender(), metadataDTO); //TODO: or send to "chat" topic
        log.info("Message sent to sender: {}", metadataDTO.getSender());
    }

    private static void validateMessage(final MessageDTO message) {
        if (StringUtils.hasText(message.getSender()) || StringUtils.hasText(message.getMessage())) {
            throw new IllegalArgumentException("Message is not valid");
        }
    }

    private static MessageMetadataDTO populateMetadata(final MessageDTO message) {
        final MessageMetadataDTO metadataDTO = new MessageMetadataDTO()
                .setTimestamp(LocalDateTime.now());
        metadataDTO.setSender(message.getSender());
        metadataDTO.setMessage(message.getMessage());
        log.info("Message metadata populated");
        return metadataDTO;
    }

}
