package com.miniinstagram.feed.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniinstagram.feed.model.Post;
import com.miniinstagram.feed.service.FeedService;

import java.util.Properties;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Arrays;
import java.time.Duration;
import java.util.List;

public class FeedConsumer implements Runnable {

    private final FeedService feedService = new FeedService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "feed-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // Subscribe to all relevant topics
        consumer.subscribe(Arrays.asList("user-events", "new-post-events", "post-deleted-events"));
        System.out.println("FeedConsumer started and listening to all topics...");

        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> record : records) {
                try {
                    JsonNode node = mapper.readTree(record.value());
                    String topic = record.topic();

                    switch (topic) {
                    case "user-events": {
                        String type = node.get("type").asText();
                        long followerId = node.get("followerId").asLong();
                        long followeeId = node.get("followeeId").asLong();

                        if ("NEW_FOLLOW".equals(type)) {
                            // STEP 1: ADD THIS LINE to save the relationship in Redis!
                            feedService.addFollower(followeeId, followerId); 
                            
                            feedService.addFollowing(followerId, followeeId);
                            // STEP 2: Now preload existing posts
                            feedService.preloadFolloweePosts(followerId, followeeId);
                            
                            System.out.println("User " + followerId + " followed " + followeeId + ". Relationship saved and feed preloaded.");
                        } else if ("UNFOLLOW".equals(type)) {
                            // STEP 3: ADD THIS LINE to remove the relationship
                            feedService.removeFollower(followeeId, followerId);
                            feedService.removeFollowing(followerId, followeeId);
                            feedService.removeFolloweePosts(followerId, followeeId);
                            System.out.println("User " + followerId + " unfollowed " + followeeId + ". Feed updated.");
                        }
                        break;
                    }

                        case "new-post-events": {
                        	long idFromKafka = node.get("postId").asLong();
                            long authorId = node.get("userId").asLong();
                            String content = node.get("content").asText();

                            // Build Post object
                            Post post = new Post();
                            post.setId(idFromKafka);// assume constructor sets id inside FeedService
                            post.setAuthorId(authorId);
                            post.setContent(content);

                            // Get followers from FeedService/UserService
                            List<Long> followers = feedService.getFollowers(authorId);

                            feedService.pushNewPostToFollowers(post, followers);
                            System.out.println("--New post pushed to followers: " + content);
                            break;
                        }

                        case "post-deleted-events": {
                            long postId = node.get("postId").asLong();
                            feedService.removePostFromFeeds(postId);
                            System.out.println("--Post removed from feeds: " + postId);
                            break;
                        }

                        default:
                            System.out.println("--Unknown topic: " + topic);
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}