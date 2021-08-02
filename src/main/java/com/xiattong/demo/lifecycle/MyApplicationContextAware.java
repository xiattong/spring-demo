package com.xiattong.demo.lifecycle;

import com.xiattong.springframework.context.XTApplicationContext;
import com.xiattong.springframework.context.XTApplicationContextAware;

/**
 * @author ：xiattong
 * @description：演示获取 ApplicationContext
 * @version: $
 * @date ：Created in 2021/8/1 18:08
 * @modified By：
 */
public class MyApplicationContextAware implements XTApplicationContextAware {

    private XTApplicationContext applicationContext;

    @Override
    public void setApplicationContext(XTApplicationContext applicationContext) throws RuntimeException {
        this.applicationContext = applicationContext;
    }

    public XTApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
