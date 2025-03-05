package com.pet.project.kafkamessenger.service.kafka;

import com.pet.project.kafkamessenger.dto.MessageDTO;
import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;
import com.pet.project.kafkamessenger.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumer {

    private static final Map<String, Integer> MOCK_USER_DB = Map.of("user", 0, "user1", 1, "user2", 2);
    private static final String TOPIC = "chat";

    private final KafkaTemplate<String, MessageMetadataDTO> kafkaTemplate;

    @KafkaListener(groupId = "processors", topics = "messages")
    public void appendMetadata(ConsumerRecord<String, MessageDTO> record) {
        MessageDTO message = record.value();
        log.info("Message received: {}", message);
        final MessageMetadataDTO metadataDTO = populateMetadata(message);
        scalePartitions(MOCK_USER_DB.get(metadataDTO.getReceiver()));
        kafkaTemplate.send(TOPIC, MOCK_USER_DB.get(metadataDTO.getReceiver()), metadataDTO.getSender(), metadataDTO);
//        kafkaTemplate.send(TOPIC, metadataDTO);
        log.info("Message sent by sender: {} to receiver {}", metadataDTO.getSender(), metadataDTO.getReceiver());
    }

    @KafkaListener(groupId = "notifications", topics = "messages")
    public void sendNotification(ConsumerRecord<String, MessageDTO> record) {
        //mocked method
        log.info("Notification sent");
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

    private void scalePartitions(final Integer receiverPartition) {
        final int currentPartitions = KafkaUtil.getPartitionCount(TOPIC);

        if (receiverPartition >= currentPartitions) {
            KafkaUtil.increasePartitions(TOPIC, receiverPartition + 1);
        }
    }

}
