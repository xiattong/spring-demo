package com.xiattong.springframework.context;

import com.xiattong.springframework.beans.factory.XTBeanFactory;

/**
 * @author ：xiattong
 * @description：
 * @version: $
 * @date ：Created in 2021/7/16 1:12
 * @modified By：
 */
public interface XTApplicationContext extends XTBeanFactory {

    void refresh() throws RuntimeException;
}
