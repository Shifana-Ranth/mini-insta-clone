package com.miniinstagram.follow.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FollowResponseProducer {

    private KafkaProducer<String,String> producer;
    private ObjectMapper mapper = new ObjectMapper();

    public FollowResponseProducer(){

        Properties props = new Properties();

        props.put("bootstrap.servers","localhost:9092");
        props.put("key.serializer",StringSerializer.class.getName());
        props.put("value.serializer",StringSerializer.class.getName());

        producer = new KafkaProducer<>(props);
    }

    public void sendResponse(long followerId,long followeeId,String type){

        try{

            Map<String,Object> event = new HashMap<>();

            event.put("followerId",followerId);
            event.put("followeeId",followeeId);
            event.put("type",type);

            String json = mapper.writeValueAsString(event);

            producer.send(new ProducerRecord<>("follow-response",json));

            System.out.println("Response event sent: "+json);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}