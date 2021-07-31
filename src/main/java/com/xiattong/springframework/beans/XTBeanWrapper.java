package com.xiattong.springframework.beans;

import lombok.Getter;

/**
 * @author ：xiattong
 * @description：封装创建后的对象实例
 *      代理对象（Proxy Object）或者原生对象（Original Object）都由BeanWrapper来保存
 * 在源码中 BeanWrapper 是个接口
 * @version: $
 * @date ：Created in 2021/7/16 0:40
 * @modified By：
 */
public class XTBeanWrapper {

    /** Bean 实列*/
    @Getter
    private Object wrappedInstance;


    public XTBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
