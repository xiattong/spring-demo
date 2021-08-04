package com.xiattong.springframework.beans.factory.support;

import com.xiattong.springframework.beans.factory.config.XTBeanDefinition;

/**
 * 注册 BeanDefinition 的接口
 */
public interface XTBeanDefinitionRegistry {

    /**
     * Register a new bean definition with this registry
     * @param beanName
     * @param beanDefinition
     * @throws RuntimeException
     */
    void registerBeanDefinition(String beanName, XTBeanDefinition beanDefinition)
            throws RuntimeException;


    /**
     * 源码：org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionNames
     * Return the names of all beans defined in this registry.
     * @return the names of all beans defined in this registry,
     * or an empty array if none defined
     */
    String[] getBeanDefinitionNames();
}
