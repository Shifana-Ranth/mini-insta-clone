package com.miniinstagram.notification.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniinstagram.notification.dao.NotificationDao;
import com.miniinstagram.notification.model.Notification;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class FollowConsumer implements Runnable {

    private final NotificationDao notificationDao;
    private final ObjectMapper objectMapper;

    public FollowConsumer(NotificationDao dao) {
        this.notificationDao = dao;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("user-events")); // JSON follow events

        System.out.println("FollowConsumer started, listening for follow events...");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> record : records) {
                try {
                    JsonNode node = objectMapper.readTree(record.value());
                    long followerId = node.get("followerId").asLong();
                    long followeeId = node.get("followeeId").asLong();
                    String type = node.get("type").asText();

                    if ("NEW_FOLLOW".equals(type)) {
                        // Create notification
                        notificationDao.createNotification(
                            followeeId,
                            "NEW_FOLLOW",
                            "User " + followerId + " started following you"
                        );
                        System.out.println("Notification added for user: " + followeeId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}