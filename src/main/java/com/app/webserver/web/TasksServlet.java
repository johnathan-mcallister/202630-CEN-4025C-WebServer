package com.app.webserver.web;

import com.app.webserver.model.*;
import com.app.webserver.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/tasks")
public class TasksServlet extends HttpServlet {
    private User user(HttpServletRequest req) {
        return JpaUtil.findById(User.class, (UUID) req.getSession().getAttribute("userId")).orElseThrow();
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = user(req);
        req.setAttribute("user", user);
        req.setAttribute("tasks", JpaUtil.findByField(Task.class, "user", user));
        req.getRequestDispatcher("/WEB-INF/tasks.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Task task = new Task();
            task.setUser(user(req));
            task.setTitle(req.getParameter("title"));
            task.setDescription(req.getParameter("description"));
            task.setPriority(Integer.valueOf(req.getParameter("priority")));
            task.setDueDate(DateUtil.parse(req.getParameter("dueDate")));
            JpaUtil.save(task);
            resp.sendRedirect(req.getContextPath() + "/tasks");
        } catch (RuntimeException e) {
            resp.sendRedirect(req.getContextPath() + "/tasks?error=1");
        }
    }
}
