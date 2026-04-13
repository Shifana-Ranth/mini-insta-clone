package com.miniinstagram.post.servlet;

import com.miniinstagram.post.dao.PostDao;
import com.miniinstagram.post.kafka.PostEventProducer;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class DeletePostServlet extends HttpServlet {

    private PostDao postDao = new PostDao();
    private PostEventProducer producer = new PostEventProducer(); // optional for Kafka

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String postIdStr = req.getParameter("postId");

        if (postIdStr == null || postIdStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("postId is required");
            return;
        }

        long postId;
        try {
            postId = Long.parseLong(postIdStr);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid postId");
            return;
        }

        boolean deleted = postDao.deletePost(postId);

        if (deleted) {
            // Optional: publish Kafka event for feed updates
            producer.publishPostDeletedEvent(postId);

            resp.getWriter().write("Post deleted successfully");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Post not found");
        }
    }
}