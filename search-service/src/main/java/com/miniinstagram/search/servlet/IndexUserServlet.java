package com.miniinstagram.search.servlet;
import com.miniinstagram.search.lucene.LuceneManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

public class IndexUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        String username = request.getParameter("username");
        String description = request.getParameter("description");

        System.out.println("Index request received");
        System.out.println("ID: " + id);
        System.out.println("Username: " + username);
        System.out.println("Description: " + description);

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("User received for indexing");
        try {

            LuceneManager.indexUser(id, username, description);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}