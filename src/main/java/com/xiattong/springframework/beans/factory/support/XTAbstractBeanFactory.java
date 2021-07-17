package com.xiattong.springframework.beans.factory.support;

import com.xiattong.springframework.beans.XTBeanWrapper;
import com.xiattong.springframework.beans.factory.XTBeanFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：xiattong
 * @description：
 * @version: $
 * @date ：Created in 2021/7/16 8:18
 * @modified By：
 */
public abstract class XTAbstractBeanFactory implements XTBeanFactory {

    /**
     * Cache of singleton objects created by FactoryBeans: FactoryBean name --> object
     * 对应源码：org.springframework.beans.factory.support.FactoryBeanRegistrySupport#factoryBeanObjectCache
     * 单列的 IoC 容器缓存
     */
    private final Map<String,  Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

    /**
     * Cache of unfinished FactoryBean instances: FactoryBean name --> BeanWrapper
     * 对应源码：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#factoryBeanInstanceCache
     * 通用的 IoC 容器
     */
    private final Map<String, XTBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>(16);

    @Override
    public Object getBean(String name) throws RuntimeException {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws RuntimeException {
        return (T) getBean(requiredType.getName());
    }
}
