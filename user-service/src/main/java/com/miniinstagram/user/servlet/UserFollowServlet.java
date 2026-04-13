package com.miniinstagram.user.servlet;

import com.miniinstagram.user.kafka.UserEventProducer;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class UserFollowServlet extends HttpServlet {

    private UserEventProducer producer;

    @Override
    public void init() throws ServletException {
        producer = new UserEventProducer();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session == null || session.getAttribute("userId") == null){
            resp.sendRedirect("login.jsp");
            return;
        }

        long followerId = (Long) session.getAttribute("userId"); // logged-in user
        long followeeId = Long.parseLong(req.getParameter("userId"));
        boolean isFollow = Boolean.parseBoolean(req.getParameter("isFollow")); // true = follow, false = unfollow

       // System.out.println("Follower: " + followerId);
        //System.out.println("Followee: " + followeeId);
        //System.out.println("isFollow param: " + isFollow);
        // Convert boolean to event type string
        String eventType = isFollow ? "NEW_FOLLOW" : "UNFOLLOW";

        // Send Kafka event
        producer.sendFollowEvent(followerId, followeeId, eventType);
        System.out.println("UserFollowServlet: Sent Kafka event for " + eventType + " from " + followerId + " to " + followeeId);

        try {
            Thread.sleep(1000); // wait 1 second for Kafka processing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String redirectUrl = req.getParameter("redirect");
        if(redirectUrl != null && !redirectUrl.isEmpty()) {
            resp.sendRedirect(redirectUrl);
        } else {
            resp.sendRedirect("home");
        }
        // Redirect back to home page to refresh UI
        //resp.sendRedirect("home");
    }

    @Override
    public void destroy() {
        producer.close();
    }
}
//package com.miniinstagram.user.servlet;
//
//import com.miniinstagram.user.kafka.UserEventProducer;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//
//public class UserFollowServlet extends HttpServlet {
//
//    private UserEventProducer producer;
//
//    @Override
//    public void init() throws ServletException {
//        producer = new UserEventProducer();
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//        if(session == null || session.getAttribute("userId") == null){
//            resp.sendRedirect("login.jsp");
//            return;
//        }
//
//        long followerId = (Long) session.getAttribute("userId"); // logged-in user
//        long followeeId = Long.parseLong(req.getParameter("userId"));
//        //boolean isFollow = Boolean.parseBoolean(req.getParameter("isFollow")); // true if following, false if unfollow
//
//        boolean isFollow = Boolean.parseBoolean(req.getParameter("isFollow"));
//        String eventType = isFollow ? "NEW_FOLLOW" : "UNFOLLOW";
//        producer.sendFollowEvent(followerId, followeeId, eventType);
//        //producer.sendFollowEvent(followerId, followeeId, isFollow);
//
//        // Redirect back to home to refresh the page
//        resp.sendRedirect("home");
//    }
//ß
//    @Override
//    public void destroy() {
//        producer.close();
//    }
//}