package com.miniinstagram.feed.servlet;

import com.miniinstagram.feed.model.Post;
import com.miniinstagram.feed.service.FeedService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FeedServlet extends HttpServlet {

    private final FeedService feedService = new FeedService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login first");
            return;
        }

        long userId = (Long) session.getAttribute("userId");
        List<Post> feed = feedService.getUserFeed(userId);

        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(feed));
    }
}