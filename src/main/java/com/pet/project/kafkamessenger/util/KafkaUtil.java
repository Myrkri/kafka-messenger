package com.pet.project.kafkamessenger.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class KafkaUtil {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static int getPartitionCount(String topic) {
        final Properties properties = configToKafkaBroker();

        try (AdminClient adminClient = AdminClient.create(properties)) {
            final DescribeTopicsResult result = adminClient.describeTopics(List.of(topic));
            final Map<String, TopicDescription> descriptions = result.allTopicNames().get();
            log.info("Number of partitions: {}", descriptions.size());
            return descriptions.get(topic).partitions().size();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to retrieve number of partitions from topic with error: `{}`", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void increasePartitions(String topic, int newPartitionCount) {
        final Properties config = configToKafkaBroker();

        try (AdminClient adminClient = AdminClient.create(config)) {
            adminClient.createPartitions(
                    Map.of(topic, NewPartitions.increaseTo(newPartitionCount))
            ).all().get();
            log.info("Successfully increased partitions of topic `{}`", topic);
        } catch (Exception e) {
            log.error("Failed to increase number of partitions with error: `{}`", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static Properties configToKafkaBroker() {
        final Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        return config;
    }
}
