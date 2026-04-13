<%@ page import="com.miniinstagram.user.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile • Mini-Instagram</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            background-color: #fafafa;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #262626;
        }
        .navbar {
            background: white;
            border-bottom: 1px solid #dbdbdb;
            padding: 10px 20px;
            text-align: center;
            position: sticky;
            top: 0;
            z-index: 100;
        }
        .logo { font-family: 'Cookie', cursive; font-size: 28px; text-decoration: none; color: black; }
        
        .profile-container {
            max-width: 935px;
            margin: 30px auto;
            padding: 0 20px;
        }
        .profile-header {
            display: flex;
            align-items: center;
            margin-bottom: 44px;
        }
        .profile-photo-container { flex: 1; text-align: center; }
        .profile-photo {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            object-fit: cover;
            border: 1px solid #dbdbdb;
        }
        .profile-info { flex: 2; }
        .username-row {
            display: flex;
            align-items: center;
            gap: 20px;
            margin-bottom: 20px;
        }
        .username { font-size: 28px; font-weight: 300; }
        .stats-row {
            display: flex;
            gap: 40px;
            margin-bottom: 20px;
            list-style: none;
        }
        .stats-row span { font-weight: bold; }
        .bio-row { font-size: 16px; line-height: 24px; }
        .bio-name { font-weight: bold; display: block; }
        
        .status-dot { height: 10px; width: 10px; border-radius: 50%; display: inline-block; margin-right: 5px; }
        
        .action-buttons {
            display: flex;
            gap: 10px;
            margin-top: 20px;
            border-top: 1px solid #dbdbdb;
            padding-top: 20px;
        }
        .btn {
            background: white;
            border: 1px solid #dbdbdb;
            padding: 5px 15px;
            border-radius: 4px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
            color: black;
        }
        .btn-blue { background-color: #0095f6; color: white; border: none; }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Cookie&display=swap" rel="stylesheet">
</head>
<body>

<%
    User user = (User) request.getAttribute("user");
%>

    <nav class="navbar">
        <a href="home" class="logo">Mini-Instagram</a>
    </nav>

    <main class="profile-container">
        <header class="profile-header">
            <div class="profile-photo-container">
                <img src="<%= user.getProfilePhotoUrl() %>" class="profile-photo" alt="Profile Photo">
            </div>

            <section class="profile-info">
                <div class="username-row">
                    <h2 class="username"><%= user.getUsername() %></h2>
                    
                    <%-- Logic for Status --%>
                    <% if("ACTIVE".equals(user.getStatus())){ %>
                        <span style="font-size: 12px; color: #8e8e8e;">
                            <span class="status-dot" style="background-color: #78de45;"></span> Active Now
                        </span>
                    <% } else { %>
                        <span style="font-size: 12px; color: #8e8e8e;">
                            <span class="status-dot" style="background-color: #ff9f00;"></span> Pending
                        </span>
                    <% } %>
                </div>

                <ul class="stats-row">
                    <li><span><%= user.getPostCount() %></span> posts</li>
                    <li><span><%= user.getFollowerCount() %></span> followers</li>
                    <li><span><%= user.getFollowingCount() %></span> following</li>
                </ul>

                <div class="bio-row">
                    <span class="bio-name"><%= user.getUsername() %></span>
                    <p><%= user.getDescription() %></p>
                    <p style="color: #00376b; font-size: 14px;"><%= user.getEmail() %></p>
                </div>
            </section>
        </header>

        <div class="action-buttons">
        	<a href="home" class="btn btn-blue">Home</a>
            <form action="userPostsPage" method="get" style="display:inline;">
                <input type="hidden" name="userId" value="<%= user.getId() %>">
                <button type="submit" class="btn">View My Posts</button>
            </form>
         
            <a href="homeFeed" class="btn btn-blue">Home Feed</a>
            <a href="notifications" class="btn">Notifications</a>
            <a href="logout" class="btn" style="color: #ed4956;">Logout</a>
        </div>
    </main>

</body>
</html>
<%-- <%@ page import="com.miniinstagram.user.model.User" %>

<html>
<body>

<%
User user = (User) request.getAttribute("user");
%>

<h2>User Home Page</h2>

Username: <%= user.getUsername() %> <br><br>

Email: <%= user.getEmail() %> <br><br>

Description: <%= user.getDescription() %> <br><br>

Followers: <%= user.getFollowerCount() %> <br><br>

Following: <%= user.getFollowingCount() %> <br><br>

<img src="<%= user.getProfilePhotoUrl() %>" width="120">

<br><br>

Status:

<%
if("ACTIVE".equals(user.getStatus())){
%>

<span style="color:green;font-weight:bold;"> Online</span>

<%
}else{
%>

<span style="color:orange;"> Pending</span>

<%
}
%>
<a href="home">Home</a>
<br><br>
<a href="notifications">View Notifications</a>
<br><br>
<a href="logout">Logout</a>

<!-- NEW: Post Button -->
<form action="userPostsPage" method="get">
    <input type="hidden" name="userId" value="<%= user.getId() %>">
    <input type="submit" value="View Posts">
</form>
<a href="homeFeed" style="text-decoration: none;">
    <button style="background-color: #0095f6; color: white; border: none; padding: 8px 20px; border-radius: 4px; font-weight: bold; cursor: pointer;">
        View Home Feed
    </button>
</a>
<br><br>
</body>
</html> --%>