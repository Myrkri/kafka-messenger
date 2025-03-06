package com.pet.project.kafkamessenger.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class KafkaUtil {

    public static int getPartitionCount(String topic, String bootstrapServer) {
        final Properties properties = configToKafkaBroker(bootstrapServer);

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

    public static void increasePartitions(String topic, int newPartitionCount, String bootstrapServer) {
        final Properties config = configToKafkaBroker(bootstrapServer);

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

    private static Properties configToKafkaBroker(String bootstrapServer) {
        final Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return config;
    }
}
