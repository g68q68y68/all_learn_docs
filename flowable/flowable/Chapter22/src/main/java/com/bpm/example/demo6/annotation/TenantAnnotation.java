package com.bpm.example.demo6.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//业务方切换注解
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantAnnotation {
    String tenantId() default "";
}
