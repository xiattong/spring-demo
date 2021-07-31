package com.xiattong.springframework.web.servlet.v4;

import com.sun.istack.internal.Nullable;
import com.xiattong.springframework.context.annotation.XTAnnotationConfigApplicationContext;
import com.xiattong.springframework.web.servlet.XTHandlerAdapter;
import com.xiattong.springframework.web.servlet.XTHandlerMapping;
import com.xiattong.springframework.web.servlet.XTViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author ：xiattong
 * @description：核心功能类
 *
 * @version: $
 * @date ：Created in 2021/6/30 17:12
 * @modified By：
 */
public class XTDispatcherServlet extends HttpServlet {

    /**
     * List of HandlerMappings used by this servlet
     * 源码： org.springframework.web.servlet.DispatcherServlet#handlerMappings
     */

    @Nullable
    private List<XTHandlerMapping> handlerMappings;

    /** List of HandlerAdapters used by this servlet */
    @Nullable
    private List<XTHandlerAdapter> handlerAdapters;

    /** List of ViewResolvers used by this servlet */
    @Nullable
    private List<XTViewResolver> viewResolvers;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化 IoC
        XTAnnotationConfigApplicationContext context = new XTAnnotationConfigApplicationContext(config.getInitParameter("contextConfigLocation"));
        //
        context.getBean("demoService");
    }
}