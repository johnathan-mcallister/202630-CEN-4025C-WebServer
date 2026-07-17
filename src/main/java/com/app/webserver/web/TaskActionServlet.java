package com.app.webserver.web;

import com.app.webserver.model.Task;
import com.app.webserver.util.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/task/action")
public class TaskActionServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID userId = (UUID) req.getSession().getAttribute("userId");
        UUID taskId = UUID.fromString(req.getParameter("id"));
        String action = req.getParameter("action");

        JpaUtil.runInTransactionVoid(em -> {
            Task task = em.createQuery(
                    "SELECT t FROM Task t WHERE t.id = :id AND t.user.id = :userId", Task.class)
                    .setParameter("id", taskId).setParameter("userId", userId)
                    .getResultStream().findFirst().orElseThrow();
            if ("delete".equals(action)) {
                em.remove(task);
            } else if ("toggle".equals(action)) {
                task.setCompleted(!task.getCompleted());
            } else if ("edit".equals(action)) {
                task.setTitle(req.getParameter("title"));
                task.setDescription(req.getParameter("description"));
                task.setPriority(Integer.valueOf(req.getParameter("priority")));
                task.setDueDate(DateUtil.parse(req.getParameter("dueDate")));
            }
        });
        resp.sendRedirect(req.getContextPath() + "/tasks");
    }
}
