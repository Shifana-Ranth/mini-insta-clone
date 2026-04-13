<%@ page import="java.util.*, com.miniinstagram.user.model.User" %>
<%-- 1. FIXED ENCODING: This stops the weird characters --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    List<User> users = (List<User>) request.getAttribute("users");
    if (users == null) {
        users = new ArrayList<User>();
    }

    HttpSession sessionObj = request.getSession(false);
    if(sessionObj == null || sessionObj.getAttribute("userId") == null){
        response.sendRedirect("login.jsp");
        return;
    }
    long currentUserId = (Long) sessionObj.getAttribute("userId");
    String searchQuery = request.getParameter("q");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results • Mini-Instagram</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { 
            background-color: #fafafa; 
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #262626;
            padding-bottom: 70px;
        }

        .search-header {
            background: white; border-bottom: 1px solid #dbdbdb;
            padding: 12px 16px; position: sticky; top: 0; z-index: 1000;
        }
        .search-form {
            max-width: 600px; margin: 0 auto;
            display: flex; gap: 10px; align-items: center;
        }
        .input-wrapper { position: relative; flex-grow: 1; }
        .search-bar {
            width: 100%; background: #efefef; border: none;
            padding: 10px 12px 10px 35px; border-radius: 8px; font-size: 14px; outline: none;
        }
        .search-icon { position: absolute; left: 10px; top: 50%; transform: translateY(-50%); color: #8e8e8e; }
        .search-btn {
            background-color: #0095f6; color: white; border: none;
            padding: 8px 16px; border-radius: 8px; font-weight: 600; font-size: 14px; cursor: pointer;
        }

        .results-wrapper { max-width: 600px; margin: 20px auto; padding: 0 10px; }
        .section-title { font-size: 14px; font-weight: 600; margin-bottom: 15px; color: #8e8e8e; }

        .user-card {
            background: white; display: flex; align-items: center;
            padding: 12px 16px; border-bottom: 1px solid #efefef;
        }
        .avatar-circle {
            width: 44px; height: 44px; border-radius: 50%;
            background: #efefef; margin-right: 12px; border: 1px solid #dbdbdb;
        }
        .user-info { flex-grow: 1; }
        .username { font-weight: 600; font-size: 14px; color: black; text-decoration: none; }
        .user-desc { font-size: 13px; color: #8e8e8e; margin-top: 2px; }
        .stats { font-size: 12px; color: #8e8e8e; margin-top: 4px; }
        
        .btn-view {
            background: transparent; color: #0095f6; border: none;
            font-size: 12px; font-weight: 600; cursor: pointer; margin-top: 5px;
            display: block; padding: 0;
        }

        .btn-follow {
            background: #0095f6; color: white; border: none;
            padding: 6px 16px; border-radius: 8px; font-weight: 600; font-size: 14px; cursor: pointer;
        }
        .btn-unfollow {
            background: #efefef; color: black; border: none;
            padding: 6px 16px; border-radius: 8px; font-weight: 600; font-size: 14px; cursor: pointer;
        }

        .bottom-nav {
            position: fixed; bottom: 0; width: 100%; background: white;
            border-top: 1px solid #dbdbdb; display: flex; justify-content: space-around;
            padding: 12px 0; z-index: 1000;
        }
        .nav-icon { text-decoration: none; color: #262626; font-size: 22px; }
    </style>
</head>
<body>

    <header class="search-header">
        <form action="${pageContext.request.contextPath}/searchUsers" method="get" class="search-form">
            <div class="input-wrapper">
                <span class="search-icon">🔍</span>
                <input type="text" name="q" class="search-bar" value="<%= (searchQuery != null) ? searchQuery : "" %>" required>
            </div>
            <button type="submit" class="search-btn">Search</button>
        </form>
    </header>

    <div class="results-wrapper">
        <h2 class="section-title">Search results for "<%= (searchQuery != null) ? searchQuery : "" %>"</h2>

        <% for(User u : users) { %>
            <div class="user-card">
                <div class="avatar-circle"></div>
                
                <div class="user-info">
                    <span class="username"><%= u.getUsername() %></span>
                    <p class="user-desc"><%= (u.getDescription() != null) ? u.getDescription() : "Mini-Instagram Member" %></p>
                    <p class="stats"><%= u.getFollowerCount() %> followers &bull; <%= u.getFollowingCount() %> following</p>
                    
                    <%-- 2. Added View Profile Link here --%>
                    <form action="viewUser" method="get">
                        <input type="hidden" name="userId" value="<%= u.getId() %>">
                        <button type="submit" class="btn-view">View Profile</button>
                    </form>
                </div>

                <div class="action-btn">
                    <% if(u.getId() != currentUserId) { %>
                        <form action="user-follow" method="post">
                            <input type="hidden" name="userId" value="<%= u.getId() %>">
                            <input type="hidden" name="followerId" value="<%= currentUserId %>">
                            <input type="hidden" name="redirect" value="searchUsers?q=<%= searchQuery %>">

                            <% if(u.getIsFollowed() != null && u.getIsFollowed()) { %>
                                <input type="hidden" name="isFollow" value="false">
                                <button type="submit" class="btn-unfollow">Following</button>
                            <% } else { %>
                                <input type="hidden" name="isFollow" value="true">
                                <button type="submit" class="btn-follow">Follow</button>
                            <% } %>
                        </form>
                    <% } else { %>
                        <span style="font-size: 12px; color: #8e8e8e;">(You)</span>
                    <% } %>
                </div>
            </div>
        <% } %>
    </div>

    <nav class="bottom-nav">
        <a href="homeFeed" class="nav-icon">🏠</a>
        <a href="home" class="nav-icon">🔍</a>
        <a href="#" class="nav-icon">➕</a>
        <a href="notifications" class="nav-icon">🔔</a>
        <a href="profile" class="nav-icon">👤</a>
    </nav>

</body>
</html>
<%-- <%@ page import="java.util.*, com.miniinstagram.user.model.User" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");

    HttpSession sessionObj = request.getSession(false);
    if(sessionObj == null || sessionObj.getAttribute("userId") == null){
        response.sendRedirect("login.jsp");
        return;
    }
    long currentUserId = (Long) sessionObj.getAttribute("userId");
%>

<h2>Search Results</h2>

<% if(users == null || users.isEmpty()) { %>
    <p>No users found matching your query.</p>
<% } else { %>
    <table border="1">
        <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Followers</th>
            <th>Following</th>
            <th>Action</th>
            <th>View Posts</th>
        </tr>
        <% for(User u : users) { %>
        <tr>
            <td>
                <!-- Clicking username goes to user profile page -->
                <form action="userPostsPage" method="get" style="display:inline;">
                    <input type="hidden" name="userId" value="<%= u.getId() %>">
                    <input type="submit" value="<%= u.getUsername() %>">
                </form>
            </td>
            <td><%= u.getDescription() %></td>
            <td><%= u.getFollowerCount() %></td>
            <td><%= u.getFollowingCount() %></td>
            <td>
    <% if(u.getIsFollowed() != null) { %>
        <form action="user-follow" method="post">
            <input type="hidden" name="userId" value="<%= u.getId() %>">
            <input type="hidden" name="followerId" value="<%= currentUserId %>">

            1️⃣ Add redirect to stay on search page
            <input type="hidden" name="redirect" value="searchUsers?q=<%= request.getParameter("q") %>">

            <% if(u.getIsFollowed()) { %>
                <input type="hidden" name="isFollow" value="false">
                <input type="submit" value="Unfollow">
            <% } else { %>
                <input type="hidden" name="isFollow" value="true">
                <input type="submit" value="Follow">
            <% } %>
        </form>
    <% } else { %>
        <span>—</span>
    <% } %>
</td>
            <td>
                <form action="viewUser" method="get">
    <input type="hidden" name="userId" value="<%= u.getId() %>">
    <input type="submit" value="View Profile">
</form>
            </td>
        </tr>
        <% } %>
    </table>
<% } %> --%>