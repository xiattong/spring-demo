package com.xiattong.springframework.web.servlet.v3;

import com.xiattong.springframework.annotation.*;
import com.xiattong.springframework.utils.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

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
    //保存用户配置好的配置文件
    private Properties contextConfig = new Properties();

    //缓存从包路径下扫描的全类名
    private Set<String> classSet= new HashSet<String>();

    //保存所有扫描的类的实例
    private Map<String,Object> ioc = new HashMap<String,Object>();

    //保存Controller中URL和Method的关系
    private List<Handler> handlerMapping = new ArrayList<>();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // PATTERNS : 委派模式
            doDispatch(req, resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            //1. 加载配置文件
            doLoadConfig(config.getInitParameter("contextConfigLocation"));
            //2. 扫描相关的类
            doScanner(contextConfig.getProperty("scanPackage"));
            //3. 初始化 IOC 容器
            doInstance();
            //4. 依赖注入
            doAutowired();
            //5. 初始化 HandlerMapping
            initHandlerMapping();

            System.out.println("IOC:" + this.ioc);
            System.out.println("handlerMapping:" + this.handlerMapping);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载配置文件
     * @param contextConfigLocation
     */
    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            this.contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
                    this.classSet.add(className);
                }
            }
        }
    }

    /**
     * 初始化 IOC 容器
     */
    private void doInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (classSet.isEmpty()) {
            return;
        }
        for (String className : this.classSet) {
            if (!className.contains(".")) {
                continue;
            }
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(XTController.class)) {
                // Bean 初始化
                this.ioc.put(StringUtils.toLowerFirstCase(clazz.getSimpleName()), clazz.newInstance());
            } else if (clazz.isAnnotationPresent(XTService.class))  {
                XTService service = clazz.getAnnotation(XTService.class);
                System.out.println(clazz.getAnnotations());
                // 自定义的 beanName
                String beanName = service.value();
                if ("".equals(beanName.trim())) {
                    beanName = StringUtils.toLowerFirstCase(clazz.getSimpleName());
                }
                Object instance = clazz.newInstance();
                this.ioc.put(beanName, instance);
                // 给接口也初始化实例
                for (Class classInterface : clazz.getInterfaces()) {
                    if (!this.ioc.containsKey(StringUtils.toLowerFirstCase(classInterface.getSimpleName()))) {
                        this.ioc.put(StringUtils.toLowerFirstCase(classInterface.getSimpleName()), instance);
                    }
                }
            }
        }
    }

    /**
     * 依赖注入
     */
    private void doAutowired() throws IllegalAccessException {
        for (Object object : this.ioc.values()) {
            Class<?> clazz = object.getClass();
            if (clazz.isAnnotationPresent(XTController.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(XTAutowired.class)) {
                        XTAutowired autowired = field.getAnnotation(XTAutowired.class);
                        String beanName = autowired.value();
                        if ("".equals(beanName)) {
                            beanName = StringUtils.toLowerFirstCase(field.getType().getSimpleName());

                        }
                        field.setAccessible(true);
                        field.set(this.ioc.get(StringUtils.toLowerFirstCase(clazz.getSimpleName())), this.ioc.get(beanName));
                    }
                }
            }
        }
    }

    /**
     * 初始化 handlerMapping
     */
    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            String baseUrl = "";
            Class<?> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(XTRequestMapping.class)) {
                XTRequestMapping requestMapping = clazz.getAnnotation(XTRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(XTRequestMapping.class)) {
                    XTRequestMapping requestMapping = method.getAnnotation(XTRequestMapping.class);
                    String url = ("/" + baseUrl + requestMapping.value()).replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(url);
                    this.handlerMapping.add(new Handler(entry.getValue(), method, pattern));
                }
            }
        }
    }

    /**
     * 请求分发控制
     * @param req
     * @param resp
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InvocationTargetException, IllegalAccessException {
        Handler handler = getHandler(req);
        if (Objects.isNull(handler)) {
            resp.getWriter().write("404! Not found！！");
            return;
        }
        Method method = handler.getMethod();
        // 请求参数信息
        Map<String, Object[]> requestParams = req.getParameterMap();
        // 形参数据 (名称，位置)
        Map<String, Integer> paramIndexMapping = handler.getParamIndexMapping();
        // 参数类型信息
        Class<?>[] paramTypes = method.getParameterTypes();
        // 实参数据
        Object[] params = new Object[paramIndexMapping.size()];
        for (Map.Entry<String, Integer> entry : paramIndexMapping.entrySet()) {
            if (entry.getKey().equals(HttpServletRequest.class.getName())) {
                params[entry.getValue()] = req;
            } else if (entry.getKey().equals(HttpServletResponse.class.getName())) {
                params[entry.getValue()] = resp;
            } else {
                if(requestParams.containsKey(entry.getKey())) {
                    // 参数类型转换
                    params[entry.getValue()] = paramConvert(paramTypes[entry.getValue()], requestParams.get(entry.getKey())[0]);
                }
            }
        }
        Object returnObject = method.invoke(this.ioc.get(StringUtils.toLowerFirstCase(method.getDeclaringClass().getSimpleName())),params);
        if (Objects.isNull(returnObject) || returnObject instanceof Void) {
            return;
        }
        resp.getWriter().write(returnObject.toString());
    }

    /**
     * 参数类型转换
     * @param paramType
     * @param o
     * @return
     */
    private Object paramConvert(Class<?> paramType, Object o) {
        if(o == null) {
            return null;
        }
        if (Integer.class == paramType) {
          return Integer.valueOf(o.toString());
        }
        return o;
    }

    /**
     *
     * url 与 Method 关系
     * 替代现有的 HandlerMapping
     */
    private class Handler {
        /** 方法的实列*/
        private Object controller;
        /** 方法*/
        private Method method;
        /** 匹配规则*/
        private Pattern pattern;
        /** 参数数组*/
        private Map<String, Integer> paramIndexMapping;

        protected Handler(Object controller, Method method, Pattern pattern) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            initParamIndexMapping(method);
        }

        private void initParamIndexMapping(Method method) {
            this.paramIndexMapping = new HashMap<>(16);
            // 获取方法形参列表
            Class<?>[] parameterTypes =  method.getParameterTypes();
            // 拿到参数声明
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> clazz = parameterTypes[i];
                if (clazz == HttpServletRequest.class || clazz == HttpServletResponse.class) {
                    this.paramIndexMapping.put(clazz.getName(),i);
                } else {
                    Annotation[] paramAn = paramAnnotations[i];
                    for (Annotation pa : paramAn) {
                        if (pa instanceof XTRequestParam) {
                            String paramName = ((XTRequestParam) pa).value();
                            if (!"".equals(paramName.trim())) {
                                this.paramIndexMapping.put(paramName,i);
                            }
                        }
                    }
                }
            }
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public Map<String, Integer> getParamIndexMapping() {
            return paramIndexMapping;
        }

        public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
            this.paramIndexMapping = paramIndexMapping;
        }
    }

    /**
     * 根据请求获取 Handler
     * @param req
     * @return
     */
    private Handler getHandler(HttpServletRequest req) {
        if (this.handlerMapping.isEmpty()) {
            return null;
        }
        // 获取请求的 url
        String url = req.getRequestURI().replace(req.getContextPath(),"").replaceAll("/+","/");
        Optional<Handler> handler = this.handlerMapping.stream().filter(item -> {
            return item.getPattern().matcher(url).matches();
        }).findFirst();

        if (handler.isPresent()) {
            return handler.get();
        } else {
            return null;
        }
    }
}