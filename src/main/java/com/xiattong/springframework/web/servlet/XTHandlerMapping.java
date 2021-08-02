package com.xiattong.springframework.web.servlet;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

@Getter
@Setter
public class XTHandlerMapping {
    /** 方法的实列*/
    private Object controller;
    /** 方法*/
    private Method method;
    /** 匹配规则*/
    private Pattern pattern;

    public XTHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
