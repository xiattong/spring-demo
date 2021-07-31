package com.xiattong.demo.lifecycle;

import com.xiattong.springframework.beans.factory.config.XTBeanPostProcessor;

/**
 * @author ：xiattong
 * @description：
 * @version: $
 * @date ：Created in 2021/7/31 18:34
 * @modified By：
 */
public class MyBeanPostProcessor implements XTBeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws RuntimeException {
        System.out.println("MyBeanPostProcessor-before : " + beanName + "," + bean);
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws RuntimeException {
        System.out.println("MyBeanPostProcessor-after : " + beanName + "," + bean);
        return bean;
    }
}
