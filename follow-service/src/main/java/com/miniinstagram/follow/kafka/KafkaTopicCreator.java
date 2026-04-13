package com.miniinstagram.follow.kafka;

import org.apache.kafka.clients.admin.*;
import java.util.*;

public class KafkaTopicCreator {

    public static void createTopic(String topicName) {

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (AdminClient admin = AdminClient.create(props)) {

            NewTopic topic = new NewTopic(topicName, 1, (short)1);
            admin.createTopics(Collections.singleton(topic));

            System.out.println("Topic created: " + topicName);

        } catch (Exception e) {
            System.out.println("Topic may already exist: " + topicName);
        }
    }
}