package com.tech.common.annotation.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 直接返回，无需经过ResponseAdvice
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectResponse {
}
