package com.xiattong.springframework.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface XTAutowired {
    String value() default "";
}
