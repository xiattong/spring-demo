package com.xiattong.springframework.web.servlet;

import com.xiattong.springframework.annotation.XTRequestParam;
import com.xiattong.springframework.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class XTHandlerAdapter {

    /**
     * 源码：org.springframework.web.servlet.HandlerAdapter#supports
     * @param handler
     * @return
     */
    public boolean supports(Object handler) {
        return handler instanceof XTHandlerMapping;
    }

    /**
     * Use the given handler to handle this request.
     * The workflow that is required may vary widely.
     * 源码：org.springframework.web.servlet.HandlerAdapter#handle
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws RuntimeException
     */
    public XTModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        XTHandlerMapping handlerMapping = (XTHandlerMapping)handler;

        /** 1. 形参列表，用于记录形参的位置*/
        Map<String, Integer> paramNameIndexMapping = new HashMap<>();
        Method method = handlerMapping.getMethod();

        // 获取方法形参列表
        Class<?>[] parameterTypes =  method.getParameterTypes();

        // 拿到参数声明
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            if (clazz == HttpServletRequest.class || clazz == HttpServletResponse.class) {
                paramNameIndexMapping.put(clazz.getName(),i);
            } else {
                Annotation[] paramAn = paramAnnotations[i];
                for (Annotation pa : paramAn) {
                    if (pa instanceof XTRequestParam) {
                        String paramName = ((XTRequestParam) pa).value();
                        if (!"".equals(paramName.trim())) {
                            paramNameIndexMapping.put(paramName, i);
                        }
                    }
                }
            }
        }

        /** 2. 构造实参列表*/
        Object[] paramValues = new Object[paramNameIndexMapping.size()];
        // URL 传递的参数列表 （参数名称，实参值）
        Map<String, String[]> reqParameterMap = request.getParameterMap();
        for (Map.Entry<String, Integer> entry : paramNameIndexMapping.entrySet()) {
            if (entry.getKey().equals(HttpServletRequest.class.getName())) {
                paramValues[entry.getValue()] = request;
            } else if (entry.getKey().equals(HttpServletResponse.class.getName())) {
                paramValues[entry.getValue()] = response;
            } else {
                if(reqParameterMap.containsKey(entry.getKey())) {
                    // 参数类型转换
                    paramValues[entry.getValue()] = StringUtils.caseStringValue(reqParameterMap.get(entry.getKey())[0], parameterTypes[entry.getValue()]);
                }
            }
        }

        /** 3. 反射调用方法*/
        Object result = method.invoke(handlerMapping.getController(), paramValues);
        if (result == null) {
            return null;
        }

        boolean isModeAndView = handlerMapping.getMethod().getReturnType() == XTModelAndView.class;
        if (isModeAndView) {
            return (XTModelAndView) result;
        }
        return null;
    }
}
