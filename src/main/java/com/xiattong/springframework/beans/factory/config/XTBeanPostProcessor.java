package com.xiattong.springframework.beans.factory.config;

/**
 * @author ：xiattong
 * @description：对象初始化事件的回调机制
 * @version: $
 * @date ：Created in 2021/7/18 0:35
 * @modified By：
 */
public interface XTBeanPostProcessor {

    /**
     * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     * The returned bean instance may be a wrapper around the original.
     * @param bean
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws RuntimeException {
        return bean;
    }

    /**
     * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     * The returned bean instance may be a wrapper around the original.
     * <p>In case of a FactoryBean, this callback will be invoked for both the FactoryBean
     * instance and the objects created by the FactoryBean (as of Spring 2.0). The
     * post-processor can decide whether to apply to either the FactoryBean or created
     * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
     * <p>This callback will also be invoked after a short-circuiting triggered by a
     * @param bean
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) throws RuntimeException {
        return bean;
    }
}
