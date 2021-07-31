package com.xiattong.springframework.beans.factory.support;

import com.sun.istack.internal.Nullable;
import com.xiattong.springframework.annotation.XTAutowired;
import com.xiattong.springframework.annotation.XTComponent;
import com.xiattong.springframework.annotation.XTController;
import com.xiattong.springframework.annotation.XTService;
import com.xiattong.springframework.beans.XTBeanWrapper;
import com.xiattong.springframework.beans.factory.config.XTBeanDefinition;
import com.xiattong.springframework.beans.factory.config.XTBeanPostProcessor;
import com.xiattong.springframework.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
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

    /**
     * 存储注册信息的 BeanDefinition
     * key : SimpleName
     */
    private final Map<String, XTBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(16);


    /** Map of singleton and non-singleton bean names, keyed by dependency type*/
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>(16);

    /**
     * List of bean definition names, in registration order
     * element: SimpleName
     */
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
        // 对应源码：org.springframework.beans.factory.support.DefaultListableBeanFactory#doGetBeanNamesForType
        List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionNames) {
            XTBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (isTypeMatch(beanDefinition.getBeanClassName(),type)) {
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
        // 对应源码：instanceWrapper = createBeanInstance(beanName, mbd, args);
        // 构造 BeanWrapper 对象
        XTBeanWrapper instanceWrapper = createBeanInstance(beanName);


        // Initialize the bean instance.
        final Object bean = instanceWrapper.getWrappedInstance();
        Object exposedObject = bean;
        try {
            // Bean 属性的依赖注入
            // 对应源码：populateBean(beanName, mbd, instanceWrapper);
            populateBean(instanceWrapper);


            // 为容器创建的Bean实例对象添加BeanPostProcessor后置处理器
            // BeanPostProcessor#postProcessBeforeInitialization 和 BeanPostProcessor#postProcessAfterInitialization 执行
            // AOP 在 AnnotationAwareAspectJAutoProxyCreator#postProcessAfterInitialization 中实现 (AbstractAutoProxyCreator#postProcessAfterInitialization)
            // 对应源码：exposedObject = initializeBean(beanName, exposedObject, mbd);
            exposedObject = initializeBean(beanName, exposedObject);
        }catch (Throwable ex) {
            throw new RuntimeException("Initialization of bean failed", ex);
        }



        return exposedObject;
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


    /**
     * Create a new instance for the specified bean
     * 对应源码：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance
     * @param beanName
     * @return
     */
    protected XTBeanWrapper createBeanInstance(String beanName) {
        Object instance = null;
        XTBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        String className = beanDefinition.getBeanClassName();
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();
            return new XTBeanWrapper(instance);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 依赖注入
     * 对应源码：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean
     * @param instanceWrapper
     */
    protected void populateBean(XTBeanWrapper instanceWrapper) {
        if (instanceWrapper == null) {
            throw new RuntimeException("Cannot apply property values to null instance");
        }

        Object instance = instanceWrapper.getWrappedClass();
        Class<?> instanceClass = instance.getClass();

        //
        if (instanceClass.isAnnotationPresent(XTComponent.class)
                || instanceClass.isAnnotationPresent(XTController.class)
                || instanceClass.isAnnotationPresent(XTService.class)) {
            Field[] fields = instanceClass.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(XTAutowired.class)) {
                    continue;
                }

                XTAutowired autowired = field.getAnnotation(XTAutowired.class);
                String autowiredBeanName = autowired.value().trim();
                if (StringUtils.isEmpty(autowiredBeanName)) {
                    autowiredBeanName = field.getType().getName();
                }
                field.setAccessible(true);


                try {
                    field.set(instance, this.getBean(autowiredBeanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Initialize the given bean instance, applying factory callbacks as well as init methods and bean post processors.
     * 对应源码：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
     * @param beanName
     * @param bean
     * @return
     */
    protected Object initializeBean(String beanName, Object bean) {
        Object wrappedBean = bean;
        if (bean != null) {
            wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        }

        // 源码中还对 init methods 做了处理 ：invokeInitMethods(beanName, wrappedBean, mbd);
        // 此处省略

        if (bean != null) {
            wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }

        return wrappedBean;
    }

    /**
     *
     * 对应源码：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization
     * @param existingBean
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws RuntimeException {

        Object result = existingBean;
        for (XTBeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            Object current = beanProcessor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     *
     * 对应源码：org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization
     * @param existingBean
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws RuntimeException {

        Object result = existingBean;
        for (XTBeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            Object current = beanProcessor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }
}
