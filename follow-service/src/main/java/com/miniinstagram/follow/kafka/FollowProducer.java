package com.miniinstagram.follow.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FollowProducer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "user-events";

    private Producer<String, String> producer;
    private ObjectMapper objectMapper;

    public FollowProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        producer = new KafkaProducer<>(props);
        objectMapper = new ObjectMapper();
    }

    public void sendFollowEvent(long followerId, long followeeId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("type", "NEW_FOLLOW");
            event.put("followerId", followerId);
            event.put("followeeId", followeeId);
            event.put("timestamp", System.currentTimeMillis());

            String json = objectMapper.writeValueAsString(event);

            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, json);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) exception.printStackTrace();
                else System.out.println("Kafka event sent: " + json);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        producer.close();
    }
}