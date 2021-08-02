package com.xiattong.springframework.context.support;

import com.xiattong.springframework.beans.factory.config.XTBeanPostProcessor;
import com.xiattong.springframework.context.XTApplicationContext;
import com.xiattong.springframework.context.XTApplicationContextAware;

/**
 * @author ：xiattong
 * @description：
 * @version: $
 * @date ：Created in 2021/8/1 17:43
 * @modified By：
 */
class XTApplicationContextAwareProcessor implements XTBeanPostProcessor {

    private final XTAbstractApplicationContext applicationContext;

    public XTApplicationContextAwareProcessor(XTAbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws RuntimeException{
        if (bean instanceof XTApplicationContextAware) {
            ((XTApplicationContextAware) bean).setApplicationContext(this.applicationContext);
        }
        return bean;
    }
}
