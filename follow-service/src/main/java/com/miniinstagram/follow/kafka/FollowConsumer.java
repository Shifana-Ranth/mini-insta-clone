package com.miniinstagram.follow.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniinstagram.follow.dao.FollowDao;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import com.miniinstagram.follow.kafka.FollowResponseProducer;
public class FollowConsumer implements Runnable {

    private final FollowDao followDao;
    private final ObjectMapper objectMapper;

    private final FollowResponseProducer responseProducer = new FollowResponseProducer();
    
    public FollowConsumer(FollowDao dao) {
        this.followDao = dao;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "follow-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("user-events"));

        System.out.println("FollowConsumer started...");

        while(true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for(ConsumerRecord<String, String> record : records){
                try{
                    JsonNode node = objectMapper.readTree(record.value());
                    long followerId = node.get("followerId").asLong();
                    long followeeId = node.get("followeeId").asLong();
                    String type = node.get("type").asText();

                    if("NEW_FOLLOW".equals(type)){
                    	System.out.println("follow event processing");
                    	boolean result = followDao.followUser(followerId, followeeId);

                    	if(result){
                    	    responseProducer.sendResponse(followerId, followeeId, "FOLLOW_CREATED");
                    	}
                    	//followDao.followUser(followerId, followeeId);
                    } else if("UNFOLLOW".equals(type)){
                    	System.out.println("Unfollow event processing");
                    	boolean result = followDao.unfollowUser(followerId, followeeId);

                    	if(result){
                    	    responseProducer.sendResponse(followerId, followeeId, "FOLLOW_REMOVED");
                    	}
                    	//followDao.unfollowUser(followerId, followeeId);
                    }

                    System.out.println("Processed event: " + type + " by user " + followerId + " for " + followeeId);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}