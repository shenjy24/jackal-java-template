package com.tech.common.annotation.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 半匿名注解，使用该注解表示可以匿名访问也可以登陆态访问
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SemiAnonymous {
}
