package com.miniinstagram.post.servlet;

import com.miniinstagram.post.dao.PostDao;
import com.miniinstagram.post.model.Post;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostsByUserIdsServlet extends HttpServlet {

    private PostDao postDao = new PostDao();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idsParam = req.getParameter("ids");

        if (idsParam == null || idsParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing ids");
            return;
        }

        String[] idsArray = idsParam.split(",");

        List<Long> userIds = new ArrayList<>();
        for (String id : idsArray) {
            userIds.add(Long.parseLong(id.trim()));
        }

        List<Post> posts = postDao.getPostsByUserIds(userIds);

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