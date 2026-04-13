package com.miniinstagram.feed.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.miniinstagram.feed.cache.RedisCache;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class FeedLikeEventConsumer implements Runnable {

    @Override
    public void run() {

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "feed-service-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList(
                "post-liked-events",
                "post-unliked-events"
        ));

        System.out.println("Feed Service Consumer Started");

        while(true){

            ConsumerRecords<String,String> records =
                    consumer.poll(Duration.ofMillis(100));

            for(ConsumerRecord<String,String> record : records){

                String topic = record.topic();
                String event = record.value();

                System.out.println("Feed received event: " + event);

                processEvent(topic,event);
            }
        }
    }
    private void processEvent(String topic,String event){

        long postId = extractValue(event,"postId");

        if(topic.equals("post-liked-events")){

            System.out.println("Updating cache for LIKE on post " + postId);

            RedisCache.updateLikeCount(postId,true);
        }

        if(topic.equals("post-unliked-events")){

            System.out.println("Updating cache for UNLIKE on post " + postId);

            RedisCache.updateLikeCount(postId,false);
        }
    }
    private long extractValue(String json,String key){

        String search = "\"" + key + "\":";

        int start = json.indexOf(search) + search.length();
        int end = json.indexOf(",",start);

        if(end == -1){
            end = json.indexOf("}",start);
        }

        return Long.parseLong(json.substring(start,end).trim());
    }
}