package com.tech.util;

import java.util.UUID;

/**
 * ID工具类
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-02
 */
public class IdUtil {

    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        // 去除UUID中的连字符
        return uuid.toString().replace("-", "");
    }
}
