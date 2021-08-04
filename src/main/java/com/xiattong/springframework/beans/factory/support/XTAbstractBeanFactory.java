package com.xiattong.springframework.beans.factory.support;

import com.xiattong.springframework.beans.XTBeanWrapper;
import com.xiattong.springframework.beans.factory.XTBeanFactory;
import com.xiattong.springframework.beans.factory.config.XTBeanDefinition;
import com.xiattong.springframework.beans.factory.config.XTBeanPostProcessor;
//import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：xiattong
 * @description：
 * @version: $
 * @date ：Created in 2021/7/16 8:18
 * @modified By：
 */
//@Slf4j
public abstract class XTAbstractBeanFactory extends XTDefaultSingletonBeanRegistry implements XTBeanFactory {

    /**
     * Cache of singleton objects created by FactoryBeans: FactoryBean name --> object
     * 源码：org.springframework.beans.factory.support.FactoryBeanRegistrySupport#factoryBeanObjectCache
     * 在源码中，这是用来存储 由 FactoryBeans 创建的 单例对象的 缓存。
     *
     * 教材表述为: 单列的 IoC 容器缓存
     * 我认为教材的表述是有问题的，单列的 IoC 容器缓存在源码中实际是：org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#singletonObjects
     *
     */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

    /**
     * Cache of unfinished FactoryBean instances: FactoryBean name --> BeanWrapper
     * 源码：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#factoryBeanInstanceCache
     * 通用的 IoC 容器
     */
    private final Map<String, XTBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>(16);

    /**
     * 存储所有 XTBeanPostProcessor 实列
     * 源码：org.springframework.beans.factory.support.AbstractBeanFactory#beanPostProcessors
     * BeanPostProcessors to apply in createBean
     */
    private final List<XTBeanPostProcessor> beanPostProcessors = new ArrayList<>(16);

    /**
     * 获取 BeanDefinition,由 XTDefaultListableBeanFactory 实现
     *
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    protected abstract XTBeanDefinition getBeanDefinition(String beanName) throws RuntimeException;


    /**
     * 源码：org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean
     * @param name
     * @return
     * @throws RuntimeException
     */
    @Override
    public Object getBean(String name) throws RuntimeException {
        try {
            // 生成通知事件
            Object sharedInstance = getSingleton(name);
            if(sharedInstance == null) {
                // 实例化，并保存到 singletonObjects 中
                sharedInstance = getSingleton(name, () -> {
                    try {
                        return createBean(name);
                    }
                    catch (RuntimeException ex) {
                        // Explicitly remove instance from singleton cache: It might have been put there
                        // eagerly by the creation process, to allow for circular reference resolution.
                        // Also remove any beans that received a temporary reference to the bean.
                        destroySingleton(name);
                        throw ex;
                    }
                });

            }
            return sharedInstance;

        } catch (Exception e) {
            e.printStackTrace();
            //log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws RuntimeException {
        return (T) getBean(requiredType.getName());
    }

    @Override
    public boolean isTypeMatch(String beanName, Class<?> typeToMatch) {
        try {
            Object instance = Class.forName(beanName).newInstance();
            return typeToMatch.isInstance(instance);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 源码：org.springframework.beans.factory.support.AbstractBeanFactory#addBeanPostProcessor
     * @param beanPostProcessor
     */
    public void addBeanPostProcessor(XTBeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * 源码：org.springframework.beans.factory.support.AbstractBeanFactory#getBeanPostProcessors
     * @return
     */
    public List<XTBeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }


    /**
     * Create a bean instance for the given bean definition
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    protected abstract Object createBean(String beanName)  throws RuntimeException;
}

