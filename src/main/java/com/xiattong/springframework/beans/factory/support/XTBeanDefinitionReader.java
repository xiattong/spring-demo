package com.xiattong.springframework.beans.factory.support;

import com.xiattong.springframework.beans.factory.config.XTBeanDefinition;
import com.xiattong.springframework.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author ：xiattong
 * @description：对配置文件进行查找、读取、解析
 * @version: $
 * @date ：Created in 2021/7/16 8:54
 * @modified By：
 */
@Slf4j
public class XTBeanDefinitionReader {

    private final XTBeanDefinitionRegistry registry;

    private List<String> registryBeanClass = new ArrayList<>(16);

    private final String SCAN_NAME = "scanPackage";

    private Properties config;

    public XTBeanDefinitionReader(XTBeanDefinitionRegistry registry) {
        // 这个 registry 是 XTAbstractApplicationContext
        // 最终通过调用 XTAbstractApplicationContext#registerBeanDefinition 方法注册 BeanDefinition
        this.registry = registry;
    }

    /**
     *
     * @param configLocations
     */
    private void scan(String... configLocations) {
        // 通过 URL 定位找到配置文件，并解析出资料路径
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:",""));
        this.config = new Properties();
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
        }
        doScan(config.getProperty(SCAN_NAME));
    }


    /**
     *
     * 源码：org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan
     * @param basePackages
     */
    private void doScan(String basePackages) {
        if (Objects.isNull(basePackages)) {
            log.error("未配置资源路径");
            return;
        }
        // 扫描资源路径下待加载的类
        URL url = this.getClass().getClassLoader().getResource("/" + basePackages.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScan(basePackages + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class"))  {
                    continue;
                }
                // 保存全类名
                registryBeanClass.add(basePackages + "." + file.getName().replace(".class",""));
            }
        }
    }

    /**
     * 注册 BeanDefinition
     */
    private void registerBean() {
        try {
            for (String className : registryBeanClass) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }
                // 构造 XTBeanDefinition
                XTBeanDefinition beanDefinition = new XTBeanDefinition(beanClass.getName(), StringUtils.toLowerFirstCase(beanClass.getSimpleName()));
                registry.registerBeanDefinition(beanDefinition.getFactoryBeanName(), beanDefinition);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public void register(String... configLocations) {

        // 1. 扫描资源路径, 解析出需要处理的类信息
        scan(configLocations);

        // 2. 注册，把配置信息注册到 IoC 中
        registerBean();
    }

    public Properties getConfig() {
        return this.config;
    }
}
