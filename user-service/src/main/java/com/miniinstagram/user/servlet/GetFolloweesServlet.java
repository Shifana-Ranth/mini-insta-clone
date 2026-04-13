package com.miniinstagram.user.servlet;

import com.miniinstagram.user.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class GetFolloweesServlet extends HttpServlet {

    private UserDao userDao = new UserDao();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userIdParam = req.getParameter("userId");

        if (userIdParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing userId");
            return;
        }

        long userId = Long.parseLong(userIdParam);

        List<Long> followees = userDao.getFollowees(userId);

        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), followees);
    }
}