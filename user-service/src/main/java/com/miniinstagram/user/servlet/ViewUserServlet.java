package com.miniinstagram.user.servlet;

import com.miniinstagram.user.dao.UserDao;
import com.miniinstagram.user.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class ViewUserServlet extends HttpServlet {
    private UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetIdParam = request.getParameter("userId");
        HttpSession session = request.getSession(false);
        
        if (targetIdParam == null || session == null) {
            response.sendRedirect("home.jsp");
            return;
        }

        long targetUserId = Long.parseLong(targetIdParam);
        long currentUserId = (Long) session.getAttribute("userId");

        // 1. Get the profile details of the person we are visiting
        User targetUser = userDao.getUserById(targetUserId);

        // 2. Check if the logged-in user follows this person
        boolean isFollowed = userDao.getFollowees(currentUserId).contains(targetUserId);
        targetUser.setIsFollowed(isFollowed);

        // 3. Set attributes and go to the new page
        request.setAttribute("targetUser", targetUser);
        request.getRequestDispatcher("viewUser.jsp").forward(request, response);
    }
}