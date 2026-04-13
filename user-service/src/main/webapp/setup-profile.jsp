<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Setup Profile • Mini-Instagram</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            background-color: #fafafa;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            color: #262626;
        }
        .setup-container {
            background: white;
            border: 1px solid #dbdbdb;
            width: 400px;
            padding: 40px;
            text-align: center;
            border-radius: 1px;
        }
        .logo {
            font-family: 'Cookie', cursive;
            font-size: 32px;
            margin-bottom: 10px;
        }
        h2 {
            font-size: 18px;
            font-weight: 600;
            color: #8e8e8e;
            margin-bottom: 30px;
        }
        .form-group {
            text-align: left;
            margin-bottom: 15px;
        }
        label {
            font-size: 12px;
            font-weight: 600;
            color: #262626;
            margin-bottom: 5px;
            display: block;
        }
        input {
            width: 100%;
            padding: 10px;
            background: #fafafa;
            border: 1px solid #dbdbdb;
            border-radius: 3px;
            font-size: 14px;
        }
        input:focus {
            border-color: #a8a8a8;
            outline: none;
        }
        button {
            width: 100%;
            padding: 8px;
            background-color: #0095f6;
            border: none;
            color: white;
            font-weight: bold;
            border-radius: 8px;
            cursor: pointer;
            margin-top: 20px;
            font-size: 14px;
        }
        button:hover { background-color: #1877f2; }
        .skip-link {
            display: block;
            margin-top: 20px;
            font-size: 14px;
            color: #0095f6;
            text-decoration: none;
            font-weight: 600;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Cookie&display=swap" rel="stylesheet">
</head>
<body>

    <div class="setup-container">
        <h1 class="logo">Mini-Instagram</h1>
        <h2>Complete Your Profile</h2>
        
        <form action="setupProfile" method="post">
            <div class="form-group">
                <label>Email Address</label>
                <input type="text" name="email" placeholder="email@example.com" required>
            </div>

            <div class="form-group">
                <label>Bio / Description</label>
                <input type="text" name="description" placeholder="Tell the world about yourself...">
            </div>

            <div class="form-group">
                <label>Profile Photo URL</label>
                <input type="text" name="photo" placeholder="https://image-link.com/photo.jpg">
            </div>

            <button type="submit">Save Profile</button>
        </form>

        <a href="homeFeed" class="skip-link">Skip for now</a>
    </div>

</body>
</html>
<!-- <html>
<body>

<h2>Setup Profile</h2>

<form action="setupProfile" method="post">

Email
<input type="text" name="email">

<br><br>

Description
<input type="text" name="description">

<br><br>

Profile Photo URL
<input type="text" name="photo">

<br><br>

<button type="submit">Save Profile</button>

</form>

</body>
</html> -->