package com.miniinstagram.notification.starter;

import com.miniinstagram.notification.kafka.FollowConsumer;
import com.miniinstagram.notification.kafka.LikeNotificationConsumer;
import com.miniinstagram.notification.dao.NotificationDao;

public class NotificationServiceStarter {

    public static void main(String[] args) {

        NotificationDao dao = new NotificationDao();

        // Follow consumer
        FollowConsumer followConsumer = new FollowConsumer(dao);
        new Thread(followConsumer).start();

        // Like consumer
        LikeNotificationConsumer likeConsumer = new LikeNotificationConsumer(dao);
        new Thread(likeConsumer).start();

        System.out.println("Notification Service is running...");
    }
}