package com.app.webserver.web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/tasks", "/task/*", "/logout"})
public class AuthFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getSession(false) == null || req.getSession(false).getAttribute("userId") == null) {
            ((HttpServletResponse) response).sendRedirect(req.getContextPath() + "/");
            return;
        }
        chain.doFilter(request, response);
    }
}
