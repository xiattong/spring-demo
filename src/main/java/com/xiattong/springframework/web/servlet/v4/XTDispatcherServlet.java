package com.xiattong.springframework.web.servlet.v4;

import com.xiattong.springframework.context.annotation.XTAnnotationConfigApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：xiattong
 * @description：核心功能类
 *
 * @version: $
 * @date ：Created in 2021/6/30 17:12
 * @modified By：
 */
public class XTDispatcherServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        XTAnnotationConfigApplicationContext context = new XTAnnotationConfigApplicationContext(config.getInitParameter("contextConfigLocation"))
    }
}