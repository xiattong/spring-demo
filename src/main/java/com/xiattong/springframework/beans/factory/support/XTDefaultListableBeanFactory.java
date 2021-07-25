package com.xiattong.springframework.beans.factory.support;

import com.sun.istack.internal.Nullable;
import com.xiattong.springframework.beans.factory.config.XTBeanDefinition;
import com.xiattong.springframework.beans.factory.config.XTBeanPostProcessor;
import com.xiattong.springframework.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：xiattong
 * @description：IoC容器的典型子类
 * @version: $
 * @date ：Created in 2021/7/16 0:54
 * @modified By：
 */
@Slf4j
public class XTDefaultListableBeanFactory extends XTAbstractBeanFactory implements XTBeanDefinitionRegistry{

    /** 存储注册信息的BeanDefinition*/
    private final Map<String, XTBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(16);


    /** Map of singleton and non-singleton bean names, keyed by dependency type*/
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>(16);

    /** List of bean definition names, in registration order*/
    private volatile List<String> beanDefinitionNames = new ArrayList<>(16);

    @Override
    public void registerBeanDefinition(String beanName, XTBeanDefinition beanDefinition) throws RuntimeException {
        if (this.beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException(beanName + "已经存在！");
        }
        this.beanDefinitionMap.put(beanName, beanDefinition);
        this.beanDefinitionNames.add(beanName);
    }


    /**
     * 获取 type 类型在 IoC 容器中对应的 BeanName
     * 对应源码：org.springframework.beans.factory.support.DefaultListableBeanFactory#getBeanNamesForType(java.lang.Class<?>, boolean, boolean)
     * @param type
     * @return
     */
    @Nullable
    public String[] getBeanNamesForType(Class<?> type) {
        String[] resolvedBeanNames = this.allBeanNamesByType.get(type);
        if (resolvedBeanNames != null) {
            return resolvedBeanNames;
        }
        // 从 beanDefinitionNames 中，找到所有匹配的BeanName
        List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionNames) {
            if (isTypeMatch(beanName,type)) {
                result.add(beanName);
            }
        }
        if (!result.isEmpty()) {
            resolvedBeanNames = StringUtils.toStringArray(result);
            this.allBeanNamesByType.put(type, resolvedBeanNames);
        }
        return resolvedBeanNames;
    }

    /**
     * 对应源码：org.springframework.beans.factory.support.DefaultListableBeanFactory#getBeanDefinition
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    @Override
    protected XTBeanDefinition getBeanDefinition(String beanName) throws RuntimeException {
        XTBeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            log.trace("No bean named '" + beanName + "' found in " + this);
            throw new RuntimeException(beanName);
        }
        return bd;
    }

    /**
     * 对应源码：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(
     *          java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     * 源码中会调用 org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean 方法，咱这省了
     *
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    @Override
    protected Object createBean(String beanName) throws RuntimeException {
        // Bean 实例化
        // 对应源码：nstanceWrapper = createBeanInstance(beanName, mbd, args);


        // Bean 属性的依赖注入
        // 对应源码：populateBean(beanName, mbd, instanceWrapper);


        // 为容器创建的Bean实例对象添加BeanPostProcessor后置处理器
        // 对应源码：exposedObject = initializeBean(beanName, exposedObject, mbd);

        return null;
    }

    /**
     * 注册 BeanPostProcessor 实列
     * 对应源码：org.springframework.context.support.PostProcessorRegistrationDelegate#registerBeanPostProcessors(
     *      org.springframework.beans.factory.config.ConfigurableListableBeanFactory,
     *      org.springframework.context.support.AbstractApplicationContext
     *  )
     * @throws RuntimeException
     */
    public void registerBeanPostProcessors()  throws RuntimeException {
        // 从 IoC 容器中拿到所有 XTBeanPostProcessor 的 BeanDefinition 信息， 最终保存到 beanPostProcessors 中
        String[] postProcessorNames = this.getBeanNamesForType(XTBeanPostProcessor.class);
        if (Objects.nonNull(postProcessorNames)) {
            for (String postProcessorName : postProcessorNames) {
                XTBeanPostProcessor beanPostProcessor = (XTBeanPostProcessor)getBean(postProcessorName);
                super.addBeanPostProcessor(beanPostProcessor);
            }
        }
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
