package com.miniinstagram.follow.kafka;

import com.miniinstagram.follow.dao.FollowDao;

public class FollowServiceStarter {

    public static void main(String[] args) {

        // Create topics automatically
        KafkaTopicCreator.createTopic("user-events");
        KafkaTopicCreator.createTopic("follow-response");

        // Initialize consumer
        FollowConsumer consumer = new FollowConsumer(new FollowDao());

        // Run consumer thread
        new Thread(consumer).start();

        System.out.println("Follow Service Consumer is running and listening to Kafka events...");
    }
}