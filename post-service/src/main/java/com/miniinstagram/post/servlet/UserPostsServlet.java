package com.miniinstagram.post.servlet;

import com.miniinstagram.post.dao.PostDao;
import com.miniinstagram.post.model.Post;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

public class UserPostsServlet extends HttpServlet {

    private PostDao postDao = new PostDao();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        long userId = Long.parseLong(req.getParameter("userId"));

        List<Post> posts = postDao.getPostsByUser(userId);

        resp.setContentType("application/json");

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < posts.size(); i++) {

            Post p = posts.get(i);

            json.append("{")
                    .append("\"postId\":").append(p.getId()).append(",")
                    .append("\"userId\":").append(p.getUserId()).append(",")
                    .append("\"content\":\"").append(p.getContent()).append("\",")
                    .append("\"likes\":").append(p.getLikesCount())
                    .append("}");

            if (i < posts.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        resp.getWriter().write(json.toString());
    }
}