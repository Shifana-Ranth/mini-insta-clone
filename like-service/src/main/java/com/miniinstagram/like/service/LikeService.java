package com.miniinstagram.like.service;

import com.miniinstagram.like.dao.LikeDao;
import com.miniinstagram.like.kafka.LikeEventProducer;

public class LikeService {

    private LikeDao likeDao = new LikeDao();
    private LikeEventProducer producer = new LikeEventProducer();

    public void likePost(long userId, long postId, long postOwnerId) {

        boolean alreadyLiked = likeDao.isAlreadyLiked(userId, postId);

        if (!alreadyLiked) {

            boolean saved = likeDao.addLike(userId, postId);

            if (saved) {
                producer.publishLikeEvent(postId, userId, postOwnerId);
                System.out.println("Like saved and event published");
            }
        }
    }

    public void unlikePost(long userId, long postId) {

        boolean removed = likeDao.removeLike(userId, postId);

        if (removed) {
            producer.publishUnlikeEvent(postId, userId);
            System.out.println("Unlike event published");
        }else {
            System.out.println("Unlike failed");
        }
    }
}