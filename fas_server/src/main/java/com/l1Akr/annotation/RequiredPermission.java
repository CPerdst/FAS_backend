package com.l1Akr.annotation;

import com.l1Akr.pojo.po.RolePO;

import java.lang.annotation.*;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredPermission {

    String[] permissions() default {};

    String[] roles() default {};

    boolean requireLogin() default true;

}
