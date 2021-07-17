package com.xiattong.springframework.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@XTComponent
public @interface XTController {
    String value() default "";
}
