package com.pet.project.kafkamessenger.service.impl;

import com.pet.project.kafkamessenger.dto.MessageDTO;
import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;
import com.pet.project.kafkamessenger.service.MessengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessengerServiceImpl implements MessengerService {

    private static final String TOPIC = "messages";
    private final KafkaTemplate<String, MessageDTO> kafkaTemplate;
    private final KafkaConsumer<String, MessageMetadataDTO> consumer;

    @Override
    public void send(MessageDTO message) {
        log.info("Sending message: {}", message);
        kafkaTemplate.send(TOPIC, message);
        log.info("Message sent");
    }

    @Override
    public List<MessageMetadataDTO> getMessages(final String sender) {
        log.info("Getting messages from sender: {}", sender);
        consumer.subscribe(Collections.singletonList(sender));
        ConsumerRecords<String, MessageMetadataDTO> records = consumer.poll(Duration.ofMillis(1000));

        List<MessageMetadataDTO> messages = new ArrayList<>();
        records.records(sender)
                .forEach(record -> messages.add(record.value()));

//        consumer.commitSync();

        return messages;
    }


}
