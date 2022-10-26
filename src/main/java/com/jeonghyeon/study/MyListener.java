package com.jeonghyeon.study;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
/*
* 서블릿 리스너
* */
public class MyListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Context Initialized");
        sce.getServletContext().setAttribute("name","jeonghyeon");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("context Destroyed");
    }
}
