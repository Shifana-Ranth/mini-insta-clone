package com.miniinstagram.user.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

public class UserEventProducer {

    private final KafkaProducer<String, String> producer;
    private final String topic = "user-events";
    private final ObjectMapper mapper = new ObjectMapper();

    public UserEventProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    public void sendFollowEvent(long followerId, long followeeId, String type) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("type", type); // NEW_FOLLOW / UNFOLLOW
            event.put("followerId", followerId);
            event.put("followeeId", followeeId);
            event.put("timestamp", System.currentTimeMillis());

            String json = mapper.writeValueAsString(event);

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) exception.printStackTrace();
            });
            System.out.println("Kafka Event Sent: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        producer.close();
    }
}