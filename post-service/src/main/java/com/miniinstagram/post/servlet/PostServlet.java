package com.miniinstagram.post.servlet;

import com.miniinstagram.post.dao.PostDao;
import com.miniinstagram.post.model.Post;
import com.miniinstagram.post.kafka.PostEventProducer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class PostServlet extends HttpServlet {

    private PostDao postDao = new PostDao();
    private PostEventProducer producer = new PostEventProducer();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long userId = Long.parseLong(req.getParameter("userId"));
        String content = req.getParameter("content");
        String mediaUrl = req.getParameter("mediaUrl");

        Post post = new Post(userId, content, mediaUrl);

        long newPostId = postDao.createPost(post);

        if (newPostId!=-1) {

            producer.publishNewPostEvent(newPostId, userId, content);

            resp.getWriter().write("Post created successfully");
            //resp.sendRedirect("profile.jsp");

        } else {

            resp.getWriter().write("Post creation failed");

        }
    }
}