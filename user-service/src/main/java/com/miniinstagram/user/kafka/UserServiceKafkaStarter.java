package com.miniinstagram.user.kafka;

public class UserServiceKafkaStarter {

    public static void main(String[] args) {

        FollowResponseConsumer consumer = new FollowResponseConsumer();

        new Thread(consumer).start();

        System.out.println("UserService Kafka consumer started...");

    }
}