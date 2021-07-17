package com.xiattong.springframework.web.servlet.v1;

import com.xiattong.springframework.annotation.XTAutowired;
import com.xiattong.springframework.annotation.XTController;
import com.xiattong.springframework.annotation.XTRequestMapping;
import com.xiattong.springframework.annotation.XTService;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author ：xiattong
 * @description：核心功能类
 *
 * @version: $
 * @date ：Created in 2021/6/30 17:12
 * @modified By：
 */
@Deprecated
public class XTDispatcherServlet extends HttpServlet {

    /**
     * 定义一个 IOC 容器
     */
    private Set<String> beanSet = new HashSet<String>(16);
    private Map<String, Object> mapping = new HashMap<String, Object>(16);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        InputStream is = null;
        try {
            // 1. 加载配置文件
            Properties properties = new Properties();
            is = this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
            properties.load(is);
            // 2. 解析配置文件
            String scanPackage = properties.getProperty("scanPackage");
            // 3. 扫描
            doScanner(scanPackage);
            if(this.beanSet.isEmpty()) {
                return;
            }
            // 4. 注册
            for (String className : this.beanSet) {
                if (!className.contains(".")) {
                    continue;
                }
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(XTController.class)) {
                    // Bean 初始化
                    this.mapping.put(className, clazz.newInstance());
                    // 请求映射初始化
                    String baseUrl = "";
                    if (clazz.isAnnotationPresent(XTRequestMapping.class)) {
                        XTRequestMapping requestMapping = clazz.getAnnotation(XTRequestMapping.class);
                        baseUrl = requestMapping.value();
                    }
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(XTRequestMapping.class)) {
                            XTRequestMapping requestMapping = method.getAnnotation(XTRequestMapping.class);
                            String url = (baseUrl + requestMapping.value()).replace("/+","/");
                            mapping.put(url, method);
                        }
                    }
                } else if (clazz.isAnnotationPresent(XTService.class))  {
                    XTService service = clazz.getAnnotation(XTService.class);
                    String beanName = service.value();
                    if ("".equals(beanName)) {
                       beanName = clazz.getName();
                    }
                    Object instance = clazz.newInstance();
                    mapping.put(beanName, instance);
                    for (Class classInterface : clazz.getInterfaces()) {
                        mapping.put(classInterface.getName(), instance);
                    }
                }
            }

            // 5. 依赖注入
            for (Object object : this.mapping.values()) {
                Class<?> clazz = object.getClass();
                if (clazz.isAnnotationPresent(XTController.class)) {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(XTAutowired.class)) {
                            XTAutowired autowired = field.getAnnotation(XTAutowired.class);
                            String beanName = autowired.value();
                            if ("".equals(beanName)) {
                                beanName = field.getType().getName();

                            }
                            field.setAccessible(true);
                            field.set(this.mapping.get(clazz.getName()), this.mapping.get(beanName));
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        if (!this.mapping.containsKey(url)) {
            resp.getWriter().write("404 not found!");
            return;
        }
        Method method = (Method) this.mapping.get(url);
        Map<String, String[]> params = req.getParameterMap();
        method.invoke(this.mapping.get(method.getDeclaringClass().getName()), new Object[]{req, resp, params.get("name")[0]});
    }


    /**
     * 扫描 Bean, 初始化 IOC 容器
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." +file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                   continue;
                } else {
                    String className = scanPackage + "." + file.getName().replace(".class","");
                    this.beanSet.add(className);
                }
            }
        }

    }
}