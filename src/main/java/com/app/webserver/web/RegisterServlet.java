package com.app.webserver.web;

import com.app.webserver.model.User;
import com.app.webserver.util.JpaUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = new User();
            user.setFirstName(req.getParameter("firstName"));
            user.setLastName(req.getParameter("lastName"));
            user.setEmail(req.getParameter("email"));
            user.setPassword(req.getParameter("password"));
            if (JpaUtil.existsByField(User.class, "email", user.getEmail())) {
                throw new IllegalArgumentException("Email already exists.");
            }
            JpaUtil.save(user);
            resp.sendRedirect(req.getContextPath() + "/?created=1");
        } catch (RuntimeException e) {
            resp.sendRedirect(req.getContextPath() + "/?error=1");
        }
    }
}
