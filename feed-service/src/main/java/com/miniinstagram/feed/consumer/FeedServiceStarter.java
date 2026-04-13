package com.miniinstagram.feed.consumer;



import com.miniinstagram.feed.consumer.FeedConsumer;

public class FeedServiceStarter {
    public static void main(String[] args) {
    	System.out.println("Starting Feed Service...");

    	// Create and start the feed consumer thread
        FeedConsumer consumer = new FeedConsumer();
        Thread t = new Thread(consumer);
        t.start();

        FeedLikeEventConsumer likeConsumer = new FeedLikeEventConsumer();
        new Thread(likeConsumer).start();
        
        System.out.println("Feed Service Consumer is running and listening to Kafka events...");
    }
}