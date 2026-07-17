<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.app.webserver.model.Task,com.app.webserver.model.User" %>
<%!
private String h(Object value) {
    if (value == null) return "";
    return value.toString().replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;");
}
%>
<% User user = (User) request.getAttribute("user"); List<Task> tasks = (List<Task>) request.getAttribute("tasks"); %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><title>My Tasks</title>
    <style>body{font-family:sans-serif;max-width:900px;margin:30px auto} form{margin:10px 0;padding:10px;border:1px solid #ccc} label{display:block;margin:5px} .done{text-decoration:line-through;color:#777} button{margin:3px}</style>
</head>
<body>
<form action="logout" method="post" style="float:right;border:0"><button>Log out</button></form>
<h1><%= h(user.getFirstName()) %>'s Todo List</h1>
<% if (request.getParameter("error") != null) { %><p style="color:red">Please check the form values.</p><% } %>

<h2>Add task</h2>
<form action="tasks" method="post">
    <label>Title <input name="title" maxlength="100" required></label>
    <label>Description <input name="description" required></label>
    <label>Priority <input type="number" name="priority" min="1" max="10" value="5" required></label>
    <label>Due date <input type="datetime-local" name="dueDate" required></label>
    <button>Add</button>
</form>

<h2>Tasks</h2>
<% if (tasks.isEmpty()) { %><p>No tasks yet.</p><% } %>
<% for (Task task : tasks) { %>
<form action="task/action" method="post">
    <input type="hidden" name="id" value="<%= task.getId() %>">
    <label>Title <input name="title" value="<%= h(task.getTitle()) %>" maxlength="100" required></label>
    <label>Description <input name="description" value="<%= h(task.getDescription()) %>" required></label>
    <label>Priority <input type="number" name="priority" min="1" max="10" value="<%= task.getPriority() %>" required></label>
    <label>Due date <input type="datetime-local" name="dueDate" value="<%= task.getDueDate() %>" required></label>
    <span class="<%= task.getCompleted() ? "done" : "" %>"><%= task.getCompleted() ? "Completed" : "Not completed" %></span><br>
    <button name="action" value="edit">Save changes</button>
    <button name="action" value="toggle"><%= task.getCompleted() ? "Reopen" : "Complete" %></button>
    <button name="action" value="delete" onclick="return confirm('Delete this task?')">Delete</button>
</form>
<% } %>
</body>
</html>
