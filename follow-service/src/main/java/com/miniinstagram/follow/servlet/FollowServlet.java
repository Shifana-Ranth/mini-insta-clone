package com.miniinstagram.follow.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniinstagram.follow.dao.FollowDao;
import com.miniinstagram.follow.model.Follow;
import com.miniinstagram.follow.kafka.FollowProducer;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class FollowServlet extends HttpServlet {

    private FollowDao followDao;
    private FollowProducer followProducer;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        followDao = new FollowDao();
        followProducer = new FollowProducer();
        objectMapper = new ObjectMapper(); // Jackson mapper
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            // Parse JSON from request body
            JsonNode jsonNode = objectMapper.readTree(req.getInputStream());
            long userId = jsonNode.get("userId").asLong();      // followee
            long followerId = jsonNode.get("followerId").asLong(); // follower

            Follow follow = new Follow(userId, followerId);

            if(followDao.isFollowing(follow)) {
                out.print("{\"status\":\"already_following\"}");
                return;
            }

            boolean success = followDao.followUser(follow);
            if(success) {
                // Send Kafka event
                followProducer.sendFollowEvent(followerId, userId);
                out.print("{\"status\":\"followed\"}");
            } else {
                out.print("{\"status\":\"failed\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            JsonNode jsonNode = objectMapper.readTree(req.getInputStream());
            long userId = jsonNode.get("userId").asLong();      // followee
            long followerId = jsonNode.get("followerId").asLong(); // follower

            Follow follow = new Follow(userId, followerId);
            boolean success = followDao.unfollowUser(follow);

            if(success) {
                out.print("{\"status\":\"unfollowed\"}");
            } else {
                out.print("{\"status\":\"failed\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    public void destroy() {
        followProducer.close();
    }
}

//package com.miniinstagram.follow.servlet;
//
//import com.miniinstagram.follow.dao.FollowDao;
//import com.miniinstagram.follow.model.Follow;
//import com.miniinstagram.follow.kafka.FollowProducer;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//public class FollowServlet extends HttpServlet {
//
//    private FollowDao followDao;
//    private FollowProducer followProducer;
//
//    @Override
//    public void init() throws ServletException {
//        followDao = new FollowDao();
//        followProducer = new FollowProducer();
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        long userId = Long.parseLong(req.getParameter("userId"));      // followee
//        long followerId = Long.parseLong(req.getParameter("followerId")); // follower
//
//        Follow follow = new Follow(userId, followerId);
//        PrintWriter out = resp.getWriter();
//        resp.setContentType("application/json");
//
//        if(followDao.isFollowing(follow)) {
//            out.print("{\"status\":\"already_following\"}");
//            return;
//        }
//
//        boolean success = followDao.followUser(follow);
//        if(success) {
//            // Send Kafka event
//            followProducer.sendFollowEvent(followerId, userId);
//            out.print("{\"status\":\"followed\"}");
//        } else {
//            out.print("{\"status\":\"failed\"}");
//        }
//    }
//
//    @Override
//    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        long userId = Long.parseLong(req.getParameter("userId"));      // followee
//        long followerId = Long.parseLong(req.getParameter("followerId")); // follower
//
//        Follow follow = new Follow(userId, followerId);
//        PrintWriter out = resp.getWriter();
//        resp.setContentType("application/json");
//
//        boolean success = followDao.unfollowUser(follow);
//        if(success) {
//            out.print("{\"status\":\"unfollowed\"}");
//        } else {
//            out.print("{\"status\":\"failed\"}");
//        }
//    }
//
//    @Override
//    public void destroy() {
//        followProducer.close();
//    }
//}