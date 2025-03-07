package com.pet.project.kafkamessenger.integr;

import com.pet.project.kafkamessenger.dto.MessageDTO;
import com.pet.project.kafkamessenger.dto.MessageMetadataDTO;
import com.pet.project.kafkamessenger.service.MessengerService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MessengerServiceImplTest {

    @Autowired
    private MessengerService messengerService;

    private static final String TOPIC = "messages";

    private Consumer<String, MessageDTO> consumer;

    @BeforeEach
    void setUp() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + System.currentTimeMillis());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put("spring.json.trusted.packages", "com.pet.project.kafkamessenger.dto");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(TOPIC));
    }

    @AfterEach
    void stopKafka() {
        consumer.close();
    }

    @Test
    void testKafkaMessageFlow() {
        final MessageDTO message = new MessageDTO()
                .setSender("user")
                .setReceiver("user1")
                .setMessage("Test Message");

        final int initialCount = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10)).count();

        messengerService.send(message);

        consumer.seekToBeginning(consumer.assignment());
        final int finalCount = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10)).count();

        assertTrue(finalCount > initialCount, "Final count should be greater than initial count : " + finalCount + " > " + initialCount);
    }


    @Test
    void testGetMessagesReturnsNewMessage() throws InterruptedException {
        final List<MessageMetadataDTO> messagesBefore = messengerService.getMessages("user", "user1");
        final int initialSize = messagesBefore.size();

        final MessageDTO message = new MessageDTO()
                .setSender("user")
                .setReceiver("user1")
                .setMessage("Test Message");
        messengerService.send(message);

        Thread.sleep(10000);

        final List<MessageMetadataDTO> messagesAfter = messengerService.getMessages("user", "user1");
        final int finalSize = messagesAfter.size();

        assertTrue(finalSize > initialSize, "Final size should be greater than initial size : " + finalSize + " > " + initialSize);
    }
}
