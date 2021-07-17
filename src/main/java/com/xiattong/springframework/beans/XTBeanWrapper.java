package com.xiattong.springframework.beans;

/**
 * @author ：xiattong
 * @description：封装创建后的对象实例
 *      代理对象（Proxy Object）或者原生对象（Original Object）都由BeanWrapper来保存
 * @version: $
 * @date ：Created in 2021/7/16 0:40
 * @modified By：
 */
public class XTBeanWrapper {

    /** Bean 实列*/
    private Object wrapperInstance;

    /** Bean 类类型*/
    private Class<?> wrapperClass;

    public XTBeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return this.wrapperInstance.getClass();
    }
}
