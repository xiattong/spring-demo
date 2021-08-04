package com.xiattong.springframework.beans.factory.config;


/**
 * interface that defines a registry for shared bean instances.
 */
public interface XTSingletonBeanRegistry {

    /**
     * Return the (raw) singleton object registered under the given name.
     * @param beanName
     * @return
     */
    Object getSingleton(String beanName);
}
