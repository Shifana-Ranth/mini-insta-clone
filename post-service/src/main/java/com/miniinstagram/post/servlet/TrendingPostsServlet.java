package com.miniinstagram.post.servlet;

import com.miniinstagram.post.dao.PostDao;
import com.miniinstagram.post.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class TrendingPostsServlet extends HttpServlet {
    
    private final PostDao postDao = new PostDao();
    private final ObjectMapper mapper = new ObjectMapper();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Fetch top 10 posts from DB
        List<Post> trending = postDao.getTrendingPosts(10);
        
        // Convert to JSON
        String json = mapper.writeValueAsString(trending);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}