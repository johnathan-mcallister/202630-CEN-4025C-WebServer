package com.app.webserver.web;

import com.app.webserver.model.User;
import com.app.webserver.util.JpaUtil;
import com.app.webserver.util.PassUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email").trim().toLowerCase();
        Optional<User> user = JpaUtil.findOneByField(User.class, "email", email);
        if (user.isPresent() && PassUtil.verify(req.getParameter("password"), user.get().getPassword())) {
            req.getSession().setAttribute("userId", user.get().getId());
            resp.sendRedirect(req.getContextPath() + "/tasks");
        } else {
            resp.sendRedirect(req.getContextPath() + "/?error=1");
        }
    }
}
