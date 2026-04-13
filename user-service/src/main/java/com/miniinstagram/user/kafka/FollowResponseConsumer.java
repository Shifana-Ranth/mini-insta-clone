package com.miniinstagram.user.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniinstagram.user.dao.UserDao;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class FollowResponseConsumer implements Runnable {

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDao userDao = new UserDao();

    @Override
    public void run() {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList("follow-response"));

        System.out.println("UserService listening to follow-response topic...");

        while(true){

            ConsumerRecords<String,String> records =
                    consumer.poll(Duration.ofMillis(500));

            for(ConsumerRecord<String,String> record : records){

                try{

                    JsonNode node = mapper.readTree(record.value());

                    long followerId = node.get("followerId").asLong();
                    long followeeId = node.get("followeeId").asLong();
                    String type = node.get("type").asText();

                    System.out.println("Processing response event: " + record.value());
                    
                    if("FOLLOW_CREATED".equals(type)){

                        System.out.println("follow-Updating user follow map");
                        boolean inserted = userDao.addFollowMap(followerId, followeeId);

                        if(inserted){
                            userDao.incrementFollowerCount(followeeId);
                            userDao.incrementFollowingCount(followerId);
                        }
                        
                    }

                    if("FOLLOW_REMOVED".equals(type)){
                    	System.out.println("unfollow-ypdate map");
                    	boolean removed = userDao.removeFollowMap(followerId, followeeId);

                    	if(removed){
                    	    userDao.decrementFollowerCount(followeeId);
                    	    userDao.decrementFollowingCount(followerId);
                    	}
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}