package com.miniinstagram.post.servlet;

import com.miniinstagram.post.dao.PostDao;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

public class PostCountServlet extends HttpServlet {

    private PostDao postDao = new PostDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParam = request.getParameter("userId");

        if (userIdParam == null) {
            response.getWriter().write("0");
            return;
        }

        long userId = Long.parseLong(userIdParam);

        int count = postDao.getPostCountByUser(userId);

        response.setContentType("text/plain");
        response.getWriter().write(String.valueOf(count));
    }
}