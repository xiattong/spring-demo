package com.xiattong.springframework.beans.factory.config;


import lombok.Getter;
import lombok.Setter;

/**
 * Bean 的描述文件
 */
@Getter
@Setter
public class XTBeanDefinition {

    /**
     * @param beanClassName 类全名
     * @param factoryBeanName  SimpleName
     */
    public XTBeanDefinition(String beanClassName, String factoryBeanName) {
        this.beanClassName = beanClassName;
        this.factoryBeanName = factoryBeanName;
    }

    /** Bean 的全类名*/
    private String beanClassName;

    /** 延迟加载标记*/
    private boolean lazyInit = Boolean.FALSE;

    /** Bean 的名称， ioc 容器中的 key*/
    private String factoryBeanName;


}
