package com.miniinstagram.user.filter;

import com.miniinstagram.user.dao.UserDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import redis.clients.jedis.Jedis;
import java.io.IOException;

public class SessionFilter implements Filter {

    private UserDao userDao = new UserDao();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        // Skip filtering for login/register pages to avoid infinite loops
        String path = req.getRequestURI();
        if (path.endsWith("login.jsp") || path.endsWith("register.jsp") || path.endsWith("RegisterServlet")) {
            chain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = req.getCookies();
        String sessionId = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionId != null) {
            try (Jedis jedis = new Jedis("localhost", 6379)) {
                String userIdStr = jedis.get("session:" + sessionId);

                if (userIdStr == null) {
                    // SESSION TIMEOUT: Cookie exists but Redis key is gone
                    // We can't update status to INACTIVE here because we don't have the ID anymore
                    // So we just clear the cookie and force login
                    Cookie deadCookie = new Cookie("sessionId", "");
                    deadCookie.setMaxAge(0);
                    deadCookie.setPath("/");
                    res.addCookie(deadCookie);
                    
                    res.sendRedirect("login.jsp?error=SessionExpired");
                    return;
                }
            }
        } else {
            // NO SESSION AT ALL: Redirect to login
            res.sendRedirect("login.jsp");
            return;
        }

        // Everything is okay, go to the requested page
        chain.doFilter(request, response);
    }
}