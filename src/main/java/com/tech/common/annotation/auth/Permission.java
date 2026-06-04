package com.tech.common.annotation.auth;

import java.lang.annotation.*;

/**
 * 权限注解
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-06
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String[] value();
    boolean requireAll() default true;
}
