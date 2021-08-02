package com.xiattong.springframework.web.servlet.v4;

import com.sun.istack.internal.Nullable;
import com.xiattong.springframework.annotation.XTController;
import com.xiattong.springframework.annotation.XTRequestMapping;
import com.xiattong.springframework.context.support.XTAbstractApplicationContext;
import com.xiattong.springframework.context.support.XTClassPathXmlApplicationContext;
import com.xiattong.springframework.web.servlet.XTHandlerAdapter;
import com.xiattong.springframework.web.servlet.XTHandlerMapping;
import com.xiattong.springframework.web.servlet.XTViewResolver;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
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
        initHandlerAdapters(context);
        //初始化异常拦截器 （解析执行过程中遇到的异常）
        //initHandlerExceptionResolvers(context);
        //初始化视图预处理器（将请求解析成视图名）
        //initRequestToViewNameTranslator(context);
        //初始化视图转换器（实现动态模板的解析）
        //initViewResolvers(context);
        //FlashMap管理器 （Flash 映射管理器）
        //initFlashMapManager(context);
    }

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

    private void initHandlerAdapters(XTAbstractApplicationContext context) {

        return;
    }
}