<%@ page import="com.miniinstagram.user.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Posts • Mini-Instagram</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            background-color: #fafafa;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            padding-top: 80px;
        }
        
        .nav-header {
            background: white; border-bottom: 1px solid #dbdbdb;
            padding: 10px 20px; position: fixed; top: 0; width: 100%; z-index: 1000;
            display: flex; justify-content: space-between; align-items: center;
        }
        .logo { font-family: 'Cookie', cursive; font-size: 26px; text-decoration: none; color: black; }

        .container { max-width: 935px; margin: 0 auto; padding: 0 20px; }
        
        /* Grid Layout - 3 columns */
        .posts-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
        }

        /* The Post Card - Focused on Content */
        .post-card {
            background: white;
            border: 1px solid #dbdbdb;
            border-radius: 8px;
            display: flex;
            flex-direction: column;
            min-height: 200px; /* Ensures they look like substantial cards */
            transition: transform 0.2s;
        }
        .post-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.05); }

        /* Content Section - This is now the main part */
        .post-body {
            padding: 20px;
            flex-grow: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;
        }

        .post-caption {
            font-size: 16px;
            color: #262626; /* Solid Black */
            font-weight: 400;
            line-height: 1.4;
            word-wrap: break-word;
            overflow: hidden;
            display: -webkit-box;
            -webkit-line-clamp: 5; /* Limits text to 5 lines so cards stay even */
            -webkit-box-orient: vertical;
        }

        /* Footer Section for Stats and Actions */
        .post-footer {
            padding: 12px 15px;
            border-top: 1px solid #f0f0f0;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #fcfcfc;
            border-bottom-left-radius: 8px;
            border-bottom-right-radius: 8px;
        }

        .post-likes {
            font-size: 13px;
            font-weight: 600;
            color: #8e8e8e;
        }

        .delete-btn {
            background: none;
            border: none;
            color: #ed4956;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
            padding: 5px;
        }
        .delete-btn:hover { text-decoration: underline; }

        /* Small Image Indicator (Optional) */
        .has-image-icon {
            font-size: 10px;
            color: #0095f6;
            margin-top: 5px;
            display: block;
        }

        /* Modal & UI Buttons */
        #postModal {
            position: fixed; z-index: 2000; left: 0; top: 0; width: 100%; height: 100%;
            background-color: rgba(0,0,0,0.6); display: none; align-items: center; justify-content: center;
        }
        .modal-content { background: white; border-radius: 12px; width: 400px; padding: 25px; text-align: center; }
        textarea { width: 100%; padding: 12px; margin-bottom: 10px; border: 1px solid #dbdbdb; border-radius: 6px; font-family: inherit; }
        .add-post-btn { background: #0095f6; color: white; border: none; padding: 8px 16px; border-radius: 8px; font-weight: bold; cursor: pointer; }
        .btn-share { background: #0095f6; color: white; border: none; padding: 12px; border-radius: 8px; width: 100%; cursor: pointer; font-weight: bold; font-size: 14px; }
    </style>
    
    <script>
        var userId = "<%= request.getAttribute("userId") %>";

        function deletePost(postId) {
            if(!confirm("Are you sure you want to delete this post?")) return;
            fetch('deletePost', { 
                method: 'POST', 
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'postId=' + encodeURIComponent(postId)
            }).then(res => { if (res.ok) location.reload(); });
        }

        function loadPosts() {
            fetch('userPostsData?userId=' + encodeURIComponent(userId))
                .then(res => res.json())
                .then(posts => {
                    let container = document.getElementById("postsContainer");
                    container.innerHTML = ""; 

                    if (!posts || posts.length === 0) {
                        container.innerHTML = "<p style='grid-column:1/4; text-align:center; padding:100px; color:#8e8e8e;'>No posts found.</p>";
                        return;
                    }

                    posts.forEach(p => {
                        let card = document.createElement("div");
                        card.className = "post-card";
                        
                        // Check if an image exists just to show a tiny icon
                        let imageIndicator = p.mediaUrl ? "<span class='has-image-icon'>🖼️ Includes Media</span>" : "";

                        card.innerHTML = 
                            "<div class='post-body'>" +
                                "<div>" +
                                    "<div class='post-caption'>" + (p.content ? p.content : "<i>No text provided</i>") + "</div>" +
                                    imageIndicator +
                                "</div>" +
                            "</div>" +
                            "<div class='post-footer'>" +
                                "<div class='post-likes'>❤️ " + p.likes + "</div>" +
                                "<button class='delete-btn' onclick='deletePost(" + p.postId + ")'>Delete</button>" +
                            "</div>";
                        
                        container.appendChild(card);
                    });
                });
        }

        window.onload = loadPosts;
        function addPost() { document.getElementById("postModal").style.display = "flex"; }
        function closeModal() { document.getElementById("postModal").style.display = "none"; }
        
        function submitPost() {
            let content = document.getElementById("postContent").value;
            if (!content) { alert("Content cannot be empty!"); return; }
            
            let body = "userId=" + encodeURIComponent(userId) + "&content=" + encodeURIComponent(content);
            fetch('addPost', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: body })
            .then(res => { if (res.ok) location.reload(); });
        }
    </script>
</head>
<body>

    <nav class="nav-header">
        <a href="profile" class="logo">Mini-Instagram</a>
        <button class="add-post-btn" onclick="addPost()">+ New Post</button>
    </nav>

    <div class="container">
        <div id="postsContainer" class="posts-grid">
            </div>
    </div>

    <div id="postModal">
        <div class="modal-content">
            <h3 style="margin-bottom: 20px;">Share a Thought</h3>
            <textarea id="postContent" rows="5" placeholder="What's on your mind?"></textarea>
            <button class="btn-share" onclick="submitPost()">Post</button>
            <button style="background:none; border:none; margin-top:15px; color:#8e8e8e; cursor:pointer;" onclick="closeModal()">Cancel</button>
        </div>
    </div>

</body>
</html>
<%-- <%@ page import="com.miniinstagram.user.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User Posts</title>
    <div id="postModal" style="display:none; position:fixed; z-index:1; left:0; top:0; width:100%; height:100%; background-color:rgba(0,0,0,0.4);">
  <div style="background-color:white; margin:15% auto; padding:20px; border:1px solid #888; width:30%;">
    <h3>Create New Post</h3>
    <textarea id="postContent" rows="4" style="width:100%;" placeholder="What's on your mind?"></textarea><br><br>
    <input type="text" id="postMediaUrl" style="width:100%;" placeholder="Media URL (optional)"><br><br>
    <button onclick="submitPost()">Post</button>
    <button onclick="closeModal()">Cancel</button>
  </div>
</div>
    <script>
        // Get the userId from JSP
        var userId = "<%= request.getAttribute("userId") %>";
        // Delete a post via proxy servlet
        function deletePost(postId) {
    // The relative path depends on your URL structure
    fetch('deletePost', { 
        method: 'POST', 
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'postId=' + encodeURIComponent(postId)
    })
    .then(res => {
        if (res.ok) {
            alert("Post deleted successfully!");
            location.reload();
        } else {
            //alert("Failed to delete post! Status: " + res.status);
            res.text().then(text => {
                alert("Failed to delete post! Status: " + res.status + " Message: " + text);
            });
        }
    });
}

        // Add a new post via proxy servlet
        

        // Fetch posts and render
        function loadPosts() {
    console.log("Starting fetch for User:", userId); // DEBUG
    fetch('userPostsData?userId=' + encodeURIComponent(userId))
        .then(res => {
            console.log("Response Status:", res.status); // DEBUG
            return res.json();
        })
        .then(posts => {
            console.log("Parsed JSON data:", posts); // DEBUG
            let container = document.getElementById("postsContainer");
            container.innerHTML = ""; 

            if (!posts || posts.length === 0) {
                container.innerHTML = "<p>No posts available for this user.</p>";
                return;
            }

            posts.forEach(p => {
                let div = document.createElement("div");
                div.style.border = "1px solid #ddd";
                div.style.padding = "10px";
                div.style.margin = "5px 0";

                // Use concatenation (+) instead of backticks to avoid JSP EL conflicts
                div.innerHTML = 
                    "<p><strong>Post ID:</strong> " + p.postId + "</p>" +
                    "<p>" + (p.content ? p.content : "<i>No content</i>") + "</p>" +
                    "<p>Likes: " + p.likes + "</p>" +
                    "<button onclick='deletePost(" + p.postId + ")'>Delete Post</button>" +
                    "<hr>";
                
                container.appendChild(div);
            });
        })
        .catch(err => {
            console.error("Fetch Error:", err); // THIS WILL TELL US WHY
            document.getElementById("postsContainer").innerHTML = "<p>Error: " + err + "</p>";
        });
}

        // Load posts when page loads
        window.onload = loadPosts;
        function addPost() {
            document.getElementById("postModal").style.display = "block";
        }

        function closeModal() {
            document.getElementById("postModal").style.display = "none";
            document.getElementById("postContent").value = "";
        }

        function submitPost() {
            let content = document.getElementById("postContent").value;
            let mediaUrl = document.getElementById("postMediaUrl").value;

            if (!content) {
                alert("Please enter some content!");
                return;
            }

            let body = "userId=" + encodeURIComponent(userId) +
                       "&content=" + encodeURIComponent(content) +
                       "&mediaUrl=" + encodeURIComponent(mediaUrl);

            fetch('addPost', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: body
            }).then(res => {
                if (res.ok) {
                    alert("Post added successfully!");
                    closeModal();
                    location.reload(); // Refreshes to show the new post
                } else {
                    alert("Failed to add post!");
                }
            }).catch(err => {
                console.error(err);
                alert("Error adding post!");
            });
        }
    </script>
</head>
<body>
<h2>User Posts</h2>

<!-- Add post button -->
<button onclick="addPost()">Add Post</button>
<hr>

<!-- Container to show posts -->
<div id="postsContainer">
    <p>Loading posts...</p>
</div>

<!-- Back to profile -->

</body>
</html> --%>