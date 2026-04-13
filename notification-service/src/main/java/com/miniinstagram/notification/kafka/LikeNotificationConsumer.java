package com.miniinstagram.notification.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import com.miniinstagram.notification.dao.NotificationDao;

public class LikeNotificationConsumer implements Runnable {

    private NotificationDao notificationDao;

    public LikeNotificationConsumer(NotificationDao dao) {
        this.notificationDao = dao;
    }

    @Override
    public void run() {

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "notification-service-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList("post-liked-events"));

        System.out.println("Like Notification Consumer Started...");

        while (true) {

            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {

                String event = record.value();

                System.out.println("Liked Notification Event Received:");
                System.out.println(event);

                processLikeEvent(event);
            }
        }
    }

    private long extractValue(String json, String key) {

        String search = "\"" + key + "\":";

        int start = json.indexOf(search) + search.length();
        int end = json.indexOf(",", start);

        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return Long.parseLong(json.substring(start, end).trim());
    }

    private void processLikeEvent(String event) {

        try {

            long postOwnerId = extractValue(event, "postOwnerId");
            long likedByUserId = extractValue(event, "likedByUserId");

            String message = "User " + likedByUserId + " liked your post";

            notificationDao.createNotification(postOwnerId, "LIKE", message);

            System.out.println("Notification stored for user: " + postOwnerId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}