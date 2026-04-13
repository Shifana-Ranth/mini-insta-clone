package com.miniinstagram.like.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class LikeEventProducer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String LIKE_TOPIC = "post-liked-events";
    private static final String UNLIKE_TOPIC = "post-unliked-events";

    private Producer<String, String> producer;

    public LikeEventProducer() {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<>(props);

        System.out.println("Like Event Producer initialized");
    }

    public void publishLikeEvent(long postId, long userId, long postOwnerId) {

        String message = "{ \"postId\": " + postId +
                ", \"likedByUserId\": " + userId +
                ", \"postOwnerId\": " + postOwnerId + "}";

        ProducerRecord<String, String> record =
                new ProducerRecord<>(LIKE_TOPIC, String.valueOf(postId), message);

        producer.send(record);

        System.out.println("Like event sent: " + message);
    }

    public void publishUnlikeEvent(long postId, long userId) {

        String message = "{ \"postId\": " + postId +
                ", \"userId\": " + userId + "}";

        ProducerRecord<String, String> record =
                new ProducerRecord<>(UNLIKE_TOPIC, String.valueOf(postId), message);

        producer.send(record);

        System.out.println("Unlike event sent: " + message);
    }
}