package com.miniinstagram.user.servlet;

import com.miniinstagram.user.dao.UserDao;
import com.miniinstagram.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

public class GetUserByIdServlet extends HttpServlet {

    private UserDao userDao = new UserDao();
    private ObjectMapper mapper = new ObjectMapper();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParam = request.getParameter("userId");

        if (userIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long userId = Long.parseLong(userIdParam);

        User user = userDao.getUserById(userId);

        response.setContentType("application/json");

        if (user != null) {
            response.getWriter().write(mapper.writeValueAsString(user));
        } else {
            response.getWriter().write("{}");
        }
    }
}