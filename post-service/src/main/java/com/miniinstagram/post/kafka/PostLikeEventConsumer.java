package com.miniinstagram.post.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import com.miniinstagram.post.dao.PostDao;

public class PostLikeEventConsumer implements Runnable {

    private PostDao postDao;

    public PostLikeEventConsumer(PostDao dao) {
        this.postDao = dao;
    }

    @Override
    public void run() {

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "post-service-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(
                java.util.Arrays.asList(
                        "post-liked-events",
                        "post-unliked-events"
                )
        );

        System.out.println("Post Service Like Consumer Started...");

        while (true) {

            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {

                String topic = record.topic();
                String event = record.value();

                System.out.println("Event received from topic: " + topic);
                System.out.println(event);

                processEvent(topic, event);
            }
        }
    }
    private void processEvent(String topic, String event) {

        long postId = extractValue(event, "postId");

        if(topic.equals("post-liked-events")){

            postDao.incrementLikeCount(postId);
            System.out.println("Like count incremented for post " + postId);

        }else if(topic.equals("post-unliked-events")){

            postDao.decrementLikeCount(postId);
            System.out.println("Like count decremented for post " + postId);
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
}
    