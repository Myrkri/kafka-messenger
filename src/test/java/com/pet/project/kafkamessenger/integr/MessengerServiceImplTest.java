package com.pet.project.kafkamessenger.integr;

import com.pet.project.kafkamessenger.dto.MessageDTO;
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

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    @Autowired
    private MessengerService messengerService;

    private static final String TOPIC = "messages";

    private Consumer<String, MessageDTO> consumer;

    @BeforeEach
    void setUp() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
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

        assertTrue(finalCount > initialCount, "Final count should be greater than initial count");
    }

}
