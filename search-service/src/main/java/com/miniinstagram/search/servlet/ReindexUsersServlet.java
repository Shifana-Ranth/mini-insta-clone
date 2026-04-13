package com.miniinstagram.search.servlet;

import com.miniinstagram.search.dao.UserDao;
import com.miniinstagram.search.model.User;
import com.miniinstagram.search.lucene.LuceneManager;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ReindexUsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            //long myUserId = 2; // your own ID, so we skip yourself

            UserDao userDao = new UserDao();
            List<User> users = userDao.getAllUsers(-1);

            for (User user : users) {
                LuceneManager.indexUser(
                        String.valueOf(user.getId()),
                        user.getUsername(),
                        user.getDescription() != null ? user.getDescription() : ""
                );
            }

            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println("Reindexing completed! Total users indexed: " + users.size());

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Error while reindexing: " + e.getMessage());
        }
    }
}