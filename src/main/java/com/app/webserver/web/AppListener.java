package com.app.webserver.web;

import com.app.webserver.util.JpaUtil;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

@WebListener
public class AppListener implements ServletContextListener {
    @Override public void contextDestroyed(ServletContextEvent event) { JpaUtil.shutdown(); }
}
