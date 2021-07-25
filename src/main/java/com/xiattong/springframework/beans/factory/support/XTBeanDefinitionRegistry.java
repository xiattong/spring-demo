package com.xiattong.springframework.beans.factory.support;

import com.sun.istack.internal.Nullable;
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
}
