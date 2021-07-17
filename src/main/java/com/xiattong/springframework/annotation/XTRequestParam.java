package com.xiattong.springframework.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface XTRequestParam {
    String value() default "";
}
