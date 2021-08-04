package com.xiattong.springframework.web.servlet.v4;

import com.sun.istack.internal.Nullable;
import com.xiattong.springframework.annotation.XTController;
import com.xiattong.springframework.annotation.XTRequestMapping;
import com.xiattong.springframework.context.support.XTAbstractApplicationContext;
import com.xiattong.springframework.context.support.XTClassPathXmlApplicationContext;
import com.xiattong.springframework.web.servlet.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author ：xiattong
 * @description：核心功能类
 *
 * @version: $
 * @date ：Created in 2021/6/30 17:12
 * @modified By：
 */
@Slf4j
public class XTDispatcherServlet extends HttpServlet {

    /**
     * List of HandlerMappings used by this servlet
     * 源码： org.springframework.web.servlet.DispatcherServlet#handlerMappings
     */

    @Nullable
    private List<XTHandlerMapping> handlerMappings;

    /** List of HandlerAdapters used by this servlet */
    @Nullable
    private XTHandlerAdapter handlerAdapter;

    /** List of ViewResolvers used by this servlet */
    @Nullable
    private XTViewResolver viewResolver;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("<h1>500 Exception</h1><p>Details: "
                    + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","").replaceAll("\\s","\r\n")
                    + "</p>");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        // 根据用户请求获取 handlerMapping
        XTHandlerMapping handlerMapping = getHandlerMapping(req);

        if (handlerMapping == null) {
            processDispatchResult(req, resp, new XTModelAndView("404"));
        }

        XTModelAndView modelAndView = this.handlerAdapter.handle(req, resp, handlerMapping);

        processDispatchResult(req, resp, modelAndView);
    }

    private XTHandlerMapping getHandlerMapping(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        // 获取请求的 url
        String url = req.getRequestURI().replace(req.getContextPath(),"").replaceAll("/+","/");
        Optional<XTHandlerMapping> handlerMapping = this.handlerMappings.stream().filter(item -> {
            return item.getPattern().matcher(url).matches();
        }).findFirst();

        if (handlerMapping.isPresent()) {
            return handlerMapping.get();
        } else {
            return null;
        }
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, XTModelAndView mv) throws Exception{
        if (mv == null) {
            return;
        }
        if (this.viewResolver != null) {
            XTView view = this.viewResolver.resolveViewName(mv.getViewName(), null);
            if (view != null) {
                view.render(mv.getModel(), req, resp);
            }
        }
    }










    /**
     * 初始化 Ioc 容器和 MVC 组件
     * 源码：org.springframework.web.servlet.HttpServletBean#init
     *      --> initServletBean()
     *      --> initWebApplicationContext()
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {

        // 初始化 IoC
        XTClassPathXmlApplicationContext context = new XTClassPathXmlApplicationContext(config.getInitParameter("contextConfigLocation"));
        // 初始化 MVC 组件
        // 源码：org.springframework.web.servlet.FrameworkServlet#onRefresh
        //          --> org.springframework.web.servlet.DispatcherServlet#initStrategies
        onRefresh(context);
    }

    /**
     * 源码：org.springframework.web.servlet.DispatcherServlet#initStrategies
     *
     * Initialize the strategy objects that this servlet uses.
     * <p>May be overridden in subclasses in order to initialize further strategy objects.
     */
    protected void onRefresh(XTAbstractApplicationContext context) {
        //多文件上传的组件（处理multipart 类型的文件上传请求）
        //initMultipartResolver(context);
        //初始化本地语言环境
        //initLocaleResolver(context);
        //初始化模板处理器
        //initThemeResolver(context);
        //handlerMapping （保存 Controller 中配置的 RequestMapping 和 Method 的对应关系）
        initHandlerMappings(context);
        //初始化参数适配器 （动态匹配 Method 参数，包括类转换，动态赋值）
        initHandlerAdapters();
        //初始化异常拦截器 （解析执行过程中遇到的异常）
        //initHandlerExceptionResolvers(context);
        //初始化视图预处理器（将请求解析成视图名）
        //initRequestToViewNameTranslator(context);
        //初始化视图转换器（实现动态模板的解析）
        initViewResolvers(context);
        //FlashMap管理器 （Flash 映射管理器）
        //initFlashMapManager(context);
    }

    /**
     * handlerMapping （保存 Controller 中配置的 RequestMapping 和 Method 的对应关系）
     * @param context
     */
    private void initHandlerMappings(XTAbstractApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                if (controller == null || controller.getClass().isAnnotationPresent(XTController.class)) {
                    continue;
                }

                String baseUrl = "";
                if (controller.getClass().isAnnotationPresent(XTRequestMapping.class)) {
                    XTRequestMapping requestMapping = controller.getClass().getAnnotation(XTRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                Method[] methods = controller.getClass().getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(XTRequestMapping.class)) {
                        XTRequestMapping requestMapping = method.getAnnotation(XTRequestMapping.class);
                        String url = ("/" + baseUrl + requestMapping.value()).replaceAll("/+","/");
                        Pattern pattern = Pattern.compile(url);
                        this.handlerMappings.add(new XTHandlerMapping(controller, method, pattern));
                        log.info("HandlerMapping:" + url + ":" + method);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化参数适配器 （动态匹配 Method 参数，包括类转换，动态赋值）
     */
    private void initHandlerAdapters() {
        this.handlerAdapter = new XTHandlerAdapter();
    }

    /**
     * 初始化视图转换器（实现动态模板的解析）
     * @param context
     */
    private void initViewResolvers(XTAbstractApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        this.viewResolver = new XTViewResolver(templateRoot);
    }
}