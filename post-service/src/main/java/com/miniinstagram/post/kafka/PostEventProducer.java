package com.miniinstagram.post.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class PostEventProducer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_NEW_POST = "new-post-events";
    private static final String TOPIC_POST_DELETED = "post-deleted-events";

    private Producer<String, String> producer;

    public PostEventProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<>(props);
        System.out.println("[KafkaProducer] Initialized");
    }

    // Publish new post event
    public void publishNewPostEvent(long postId, long userId, String content) {
        String message = "{ \"postId\": " + postId + ",\"userId\": " + userId + ", \"content\": \"" + content + "\" }";
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NEW_POST, String.valueOf(userId), message);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("[KafkaProducer] Failed to send new post event: " + exception.getMessage());
            } else {
                System.out.println("[KafkaProducer] New post event sent successfully to topic " + metadata.topic() +
                        " partition " + metadata.partition() + " offset " + metadata.offset());
                System.out.println("--"+ message);
            }
        });
    }

    // Publish post deleted event
    public void publishPostDeletedEvent(long postId) {
        String message = "{ \"postId\": " + postId + " }";
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_POST_DELETED, String.valueOf(postId), message);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("[KafkaProducer] Failed to send post deleted event: " + exception.getMessage());
            } else {
                System.out.println("[KafkaProducer] Post deleted event sent successfully to topic " + metadata.topic() +
                        " partition " + metadata.partition() + " offset - " + metadata.offset()+ " for post "+ message);
                System.out.println("--"+ message);
            }
        });
    }

    public void close() {
        producer.close();
        System.out.println("[KafkaProducer] Closed");
    }
}