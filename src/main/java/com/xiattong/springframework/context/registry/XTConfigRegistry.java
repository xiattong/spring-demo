package com.xiattong.springframework.context.registry;

/**
 * BeanDefinition 注册接口
 */
public interface XTConfigRegistry {

    /**
     * 根据配置的资源路径，注册 BeanDefinition
     * @param configLocations
     */
    void register(String... configLocations);
}
