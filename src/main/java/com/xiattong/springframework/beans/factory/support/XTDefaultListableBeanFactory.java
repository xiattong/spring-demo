package com.xiattong.springframework.beans.factory.support;

import com.xiattong.springframework.beans.factory.config.XTBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：xiattong
 * @description：IoC容器的典型子类
 * @version: $
 * @date ：Created in 2021/7/16 0:54
 * @modified By：
 */
public class XTDefaultListableBeanFactory extends XTAbstractBeanFactory implements XTBeanDefinitionRegistry{

    /** 存储注册信息的BeanDefinition*/
    private final Map<String, XTBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(16);

    @Override
    public void registerBeanDefinition(String beanName, XTBeanDefinition beanDefinition) throws RuntimeException {
        if (this.beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException(beanName + "已经存在！");
        }
        this.beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 实例化非懒加载的 Bean
     * 对应源码：org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons
     * 方法中会调用 getBean()
     * @throws RuntimeException
     */
    public void preInstantiateSingletons() throws RuntimeException {
        for (Map.Entry<String, XTBeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                String beanName = beanDefinitionEntry.getKey();
                // 依赖注入
                // 读取 BeanDefinition 信息，通过发射机制创建一个实列并返回
                // 实际回返回一个包装对象：BeanWrapper
                // 装饰器模式：保留原来的 OOP 关系；可以进行扩展和增强（AOP）
                getBean(beanName);
            }
        }
    }
}
