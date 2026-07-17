<%@ page contentType="text/html;charset=UTF-8" %>
<% if (session.getAttribute("userId") != null) { response.sendRedirect("tasks"); return; } %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Todo Login</title></head>
<body>
<h1>Todo App</h1>
<% if (request.getParameter("error") != null) { %><p style="color:red">Invalid email or password.</p><% } %>
<% if (request.getParameter("created") != null) { %><p>Account created. Please log in.</p><% } %>

<h2>Log in</h2>
<form action="login" method="post">
    <label>Email: <input type="email" name="email" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <button type="submit">Log in</button>
</form>

<h2>Create account</h2>
<form action="register" method="post">
    <label>First name: <input name="firstName" required></label><br>
    <label>Last name: <input name="lastName" required></label><br>
    <label>Email: <input type="email" name="email" required></label><br>
    <label>Password: <input type="password" name="password" minlength="6" required></label><br>
    <button type="submit">Create account</button>
</form>
</body>
</html>
