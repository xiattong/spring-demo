package com.xiattong.springframework.context.annotation;

import com.xiattong.springframework.context.support.XTAbstractApplicationContext;

/**
 * @author ：xiattong
 * @description：
 * @version: $
 * @date ：Created in 2021/7/16 7:52
 * @modified By：
 */
public class XTAnnotationConfigApplicationContext extends XTAbstractApplicationContext {



    public XTAnnotationConfigApplicationContext(String... configLocations) {
        // 注册BeanDefinition，初始化 IoC 容器
        register(configLocations);
        // 初始化非懒加载的 Bean
        refresh();
    }
}
