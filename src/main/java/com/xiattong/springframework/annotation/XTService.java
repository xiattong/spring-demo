package com.xiattong.springframework.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@XTComponent
public @interface XTService {
    String value() default "";
}
