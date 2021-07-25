package com.xiattong.springframework.beans.factory.support;

import com.xiattong.springframework.beans.factory.XTObjectFactory;
import com.xiattong.springframework.beans.factory.config.XTSingletonBeanRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：xiattong
 * @description：默认的单例类注册接口实现
 * @version: $
 * @date ：Created in 2021/7/21 22:11
 * @modified By：
 */
@Slf4j
public class XTDefaultSingletonBeanRegistry implements XTSingletonBeanRegistry {

    /**
     * Cache of singleton objects: bean name --> bean instance
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * 对应源码：org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(java.lang.String)
     * @param beanName
     * @return
     */
    @Override
    public Object getSingleton(String beanName) {
        return this.singletonObjects.get(beanName);
    }

    /**
     * 对应源码 ：org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(
     *      java.lang.String, org.springframework.beans.factory.ObjectFactory<?>)
     * Return the (raw) singleton object registered under the given name,
     * creating and registering a new one if none registered yet.
     * @param beanName the name of the bean
     * @param singletonFactory the ObjectFactory to lazily create the singleton
     * with, if necessary
     * @return the registered singleton object
     */
    public Object getSingleton(String beanName, XTObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                // 创建一个，并保存
                try {
                    singletonObject = singletonFactory.getObject();
                    addSingleton(beanName, singletonObject);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    log.error("Bean 实例化异常！");
                }
            }
            return singletonObject;
        }
    }



    /**
     * 对应源码：org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingleton
     * Add the given singleton object to the singleton cache of this factory.
     * <p>To be called for eager registration of singletons.
     * @param beanName the name of the bean
     * @param singletonObject the singleton object
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
        }
    }

    /**
     * 对应源码：org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton
     * Remove the bean with the given name from the singleton cache of this factory,
     * to be able to clean up eager registration of a singleton if creation failed.
     * @param beanName the name of the bean
     */
    public void destroySingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
        }
    }
}
