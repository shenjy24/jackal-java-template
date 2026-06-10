package com.tech.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 加密工具类
 * <p>
 * 密码加密使用 BCrypt，每次 encode 自动生成随机盐，结果包含盐值，无需额外存储。
 *
 * @since 2026-05-26
 */
public final class CryptoUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private CryptoUtil() {
    }

    /**
     * 加密明文
     */
    public static String encode(String raw) {
        return ENCODER.encode(raw);
    }

    /**
     * 校验明文与密文是否匹配
     */
    public static boolean matches(String raw, String encoded) {
        return ENCODER.matches(raw, encoded);
    }

    /**
     * 校验明文与密文是否不匹配
     */
    public static boolean notMatches(String raw, String encoded) {
        return !matches(raw, encoded);
    }

    public static void main(String[] args) {
        String raw = "admin";
        System.out.println(encode(raw));
    }
}
