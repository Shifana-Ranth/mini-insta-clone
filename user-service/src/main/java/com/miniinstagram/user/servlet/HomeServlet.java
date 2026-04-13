package com.miniinstagram.user.servlet;

import com.miniinstagram.user.dao.UserDao;
import com.miniinstagram.user.model.User;
import com.miniinstagram.user.kafka.UserEventProducer;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class HomeServlet extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false); // get existing session
        if(session == null || session.getAttribute("userId") == null){
            resp.sendRedirect("login.jsp"); // no session or expired
            return;
        }

        long currentUserId = (Long) session.getAttribute("userId");
        
        // Get all users
        List<User> users = userDao.getAllUsers(currentUserId);

        // Get all users that the current user is following
        List<Long> followees = userDao.getFollowees(currentUserId);

        // Mark users as followed/unfollowed
        for(User u : users){
            if(u.getId() == currentUserId) {
                u.setIsFollowed(null); // current user, don't show button
            } else if(followees.contains(u.getId())){
                u.setIsFollowed(true);  // show "Unfollow"
            } else {
                u.setIsFollowed(false); // show "Follow"
            }
        }

        req.setAttribute("users", users);
        RequestDispatcher dispatcher = req.getRequestDispatcher("home.jsp");
        dispatcher.forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if(session == null || session.getAttribute("userId") == null){
            resp.sendRedirect("login.jsp");
            return;
        }

        long currentUserId = (Long) session.getAttribute("userId");
        long targetUserId = Long.parseLong(req.getParameter("userId"));
        String action = req.getParameter("action"); // "FOLLOW" or "UNFOLLOW"

        // Send Kafka event
        UserEventProducer producer = new UserEventProducer();
        producer.sendFollowEvent(currentUserId, targetUserId, action); 
        producer.close();

        try {
            Thread.sleep(2000); // wait 1 second for Kafka processing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //resp.sendRedirect("home");
        // Optionally, you can immediately update counts locally or wait for follow-service response
        resp.sendRedirect("home.jsp"); // refresh page
    }
}