package com.xiattong.springframework.web.servlet;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author ：xiattong
 * @description：封装页面模板和页面展示数据
 * @version: $
 * @date ：Created in 2021/8/2 11:22
 * @modified By：
 */
@Getter
@Setter
public class XTModelAndView {

    /** 页面模板名称*/
    private String viewName;

    /** 页面展示参数*/
    private Map<String, Object> model;

    public XTModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public XTModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
