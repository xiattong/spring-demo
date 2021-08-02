package com.xiattong.springframework.context;

/**
 * Interface to be implemented by any object that wishes to be notified
 *  of the {@link XTApplicationContext} that it runs in.
 */
public interface XTApplicationContextAware {

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * Invoked after population of normal bean properties but before an init callback such
     */
    void setApplicationContext(XTApplicationContext applicationContext) throws RuntimeException;
}
