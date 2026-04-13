package com.miniinstagram.like.servlet;

import com.miniinstagram.like.dao.LikeDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

public class LikedPostsServlet extends HttpServlet {

    private LikeDao likeDao = new LikeDao();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = Integer.parseInt(req.getParameter("userId"));

        List<Integer> likedPosts = likeDao.getLikedPostIdsByUser(userId);

        resp.setContentType("application/json");
        resp.getWriter().write(likedPosts.toString());
    }
}