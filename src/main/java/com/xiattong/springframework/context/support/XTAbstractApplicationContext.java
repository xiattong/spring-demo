package com.xiattong.springframework.context.support;

import com.xiattong.springframework.beans.factory.config.XTBeanDefinition;
import com.xiattong.springframework.beans.factory.support.XTBeanDefinitionReader;
import com.xiattong.springframework.beans.factory.support.XTBeanDefinitionRegistry;
import com.xiattong.springframework.beans.factory.support.XTDefaultListableBeanFactory;
import com.xiattong.springframework.context.XTApplicationContext;
import com.xiattong.springframework.context.registry.XTConfigRegistry;

/**
 * @author ：xiattong
 * @description：IoC 容器实现类的顶层抽象类，实现 IoC 容器相关的公共逻辑
 * @version: $
 * @date ：Created in 2021/7/16 0:48
 * @modified By：
 */
public abstract class XTAbstractApplicationContext implements XTApplicationContext, XTConfigRegistry, XTBeanDefinitionRegistry {

    /**
     * 源码中，beanFactory 位于 AbstractApplicationContext
     */
    private final XTDefaultListableBeanFactory beanFactory;

    /** 对配置文件进行查找、读取、解析*/
    private final XTBeanDefinitionReader reader;

    public XTAbstractApplicationContext() {
        this.reader = new XTBeanDefinitionReader(this);
        this.beanFactory = new XTDefaultListableBeanFactory();
    }

    /**
     * 这个方法会被 XTBeanDefinitionReader 调用
     * @param beanName
     * @param beanDefinition
     * @throws RuntimeException
     */
    @Override
    public void registerBeanDefinition(String beanName, XTBeanDefinition beanDefinition) throws RuntimeException {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * 注册指定资源路径下的 BeanDefinition
     * 对于源码：org.springframework.context.annotation.AnnotationConfigApplicationContext#register
     * @param configLocations
     */
    @Override
    public void register(String... configLocations) {
        this.reader.register(configLocations);
    }


    /**
     * 初始化非懒加载的 Bean
     * 对应源码：org.springframework.context.support.AbstractApplicationContext#refresh
     * @throws RuntimeException
     */
    @Override
    public void refresh() throws RuntimeException {

        // 注册 BeanPostProcessor
        // 源码由 ：org.springframework.context.support.AbstractApplicationContext#registerBeanPostProcessors
        //       org.springframework.context.support.PostProcessorRegistrationDelegate#registerBeanPostProcessors(
        //              org.springframework.beans.factory.config.ConfigurableListableBeanFactory,
        //              java.util.List<org.springframework.beans.factory.config.BeanPostProcessor>
        //       ) 实现
        // 注意，不实现 Ordered 接口的 BeanPostProcessor 由 registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);代码注册
        beanFactory.registerBeanPostProcessors();


        // 源码由 org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization 调用
        // Instantiate all remaining (non-lazy-init) singletons.
        beanFactory.preInstantiateSingletons();
    }


    /**
     * 对应源码：org.springframework.context.support.AbstractApplicationContext#getBean(java.lang.String)
     * @param name
     * @return
     * @throws RuntimeException
     */
    @Override
    public Object getBean(String name) throws RuntimeException {
        return beanFactory.getBean(name);
    }

    /**
     * 对应源码：org.springframework.context.support.AbstractApplicationContext#getBean(java.lang.Class<T>)
     * @param requiredType
     * @return
     * @throws RuntimeException
     */
    @Override
    public <T> T getBean(Class<T> requiredType) throws RuntimeException {
        return beanFactory.getBean(requiredType);
    }

}
