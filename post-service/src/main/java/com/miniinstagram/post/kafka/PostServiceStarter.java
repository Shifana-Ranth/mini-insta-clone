package com.miniinstagram.post.kafka;

import com.miniinstagram.post.dao.PostDao;

public class PostServiceStarter {

    public static void main(String[] args) {

        PostDao dao = new PostDao();

        PostLikeEventConsumer consumer =
                new PostLikeEventConsumer(dao);

        new Thread(consumer).start();

        System.out.println("Post Service Kafka Consumer Running...");
    }
}