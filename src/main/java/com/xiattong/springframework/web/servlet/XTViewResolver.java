package com.xiattong.springframework.web.servlet;

import com.xiattong.springframework.utils.StringUtils;

import java.io.File;
import java.util.Locale;

/**
 * 视图解析器: 完成模板名称和模板解析引擎的匹配
 * 功能：依据静态模板解析成动态页面；
 *      将结果以字符串的形式交给 response 输出
 */
public class XTViewResolver {
    /** 模板文件后缀*/
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    /** 模板文件根目录*/
    private File templateRootDir;
    /** 视图名称*/
    private String viewName;

    public XTViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public XTView resolveViewName(String viewName, Locale locale) throws Exception {
        this.viewName = viewName;
        if (StringUtils.isEmpty(viewName)) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new XTView(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
