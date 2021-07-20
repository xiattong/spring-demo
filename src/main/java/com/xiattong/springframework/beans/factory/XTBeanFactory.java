package com.xiattong.springframework.beans.factory;

/**
 * 单列工厂顶层设计
 */
public interface XTBeanFactory {

    /**
     * 根据bean的名字，获取在 IOC 容器中得到 bean 实例
     * @param name
     * @return
     * @throws RuntimeException
     */
    Object getBean(String name) throws RuntimeException;

    /**
     * 根据类类型，获取在 IOC 容器中得到 bean实例
     * @param requiredType
     * @return
     * @throws RuntimeException
     */
    <T> T getBean(Class<T> requiredType) throws RuntimeException;


    /**
     * 检查 beanName 的实列是不是 typeToMatch 类或其子类
     * @param beanName
     * @param typeToMatch
     * @return
     * @throws RuntimeException
     */
    boolean isTypeMatch(String beanName, Class<?> typeToMatch);
}
