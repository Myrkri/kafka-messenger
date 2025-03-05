package com.pet.project.kafkamessenger.service.impl;

import com.pet.project.kafkamessenger.dto.MessageDTO;
import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;
import com.pet.project.kafkamessenger.service.MessengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessengerServiceImpl implements MessengerService {

    /*
    Idea is that the user sends a message into the chat room, another user receives notification about it and is able to get it from getMessages api
     */

    private static final Map<String, Integer> MOCK_USER_DB = Map.of("user", 0, "user1", 1, "user2", 2);
    private static final String TOPIC = "messages";

    private final KafkaTemplate<String, MessageDTO> kafkaTemplate;
    private final KafkaConsumer<String, MessageMetadataDTO> consumer;

    @Override
    public void send(MessageDTO message) {
        validateMessage(message);
        checkUsers(message.getSender(), message.getReceiver());
        log.info("Sending message: {}", message);
        kafkaTemplate.send(TOPIC, message);
        log.info("Message sent");
    }

    @Override
    public List<MessageMetadataDTO> getMessages(final String receiver, final String sender) {
        checkUsers(sender, receiver);
        log.info("Getting messages for receiver: {}", receiver);
        final TopicPartition partition = new TopicPartition("chat", MOCK_USER_DB.get(receiver));
        consumer.assign(List.of(partition));
        //=============================================//
        //remove if not needed, responsible for reading from the beginning
        consumer.seekToBeginning(consumer.assignment());
        //=============================================//
        final var records = consumer.poll(Duration.ofMillis(1000));

        final List<MessageMetadataDTO> messages = new ArrayList<>();

        records.records(partition)
                .forEach(record -> {
                    log.info("Received record: {}", record);
                    if (record.key().equals(sender)) {
                        messages.add(record.value());
                    }
                });

//        consumer.commitSync();

        return messages;
    }

    private static void validateMessage(final MessageDTO message) {
        if (!StringUtils.hasText(message.getSender()) || !StringUtils.hasText(message.getMessage()) ||
                !StringUtils.hasText(message.getReceiver())) {
            throw new IllegalArgumentException("Message is not valid");
        }
    }

    private void checkUsers(final String receiver, final String sender) {
        if (!MOCK_USER_DB.containsKey(receiver) || !MOCK_USER_DB.containsKey(sender)) {
            throw new IllegalArgumentException("User not found");
        }
    }

}
