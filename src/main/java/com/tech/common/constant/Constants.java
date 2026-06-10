package com.tech.common.constant;

/**
 * 常量
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-02
 */
public class Constants {
    public static final String COOKIE_KEY_TOKEN = "token";
    public static final String REQ_ATT_USER = "attr-user";
    // token过期时间，单位毫秒
    public static final int TOKEN_EXPIRED_MS = 15 * 24 * 60 * 60 * 1000;
    // token刷新时间，单位毫秒
    public static final int TOKEN_REFRESH_MS = 60 * 60 * 1000;

    public static final int PAGE_NUM = 1;
    public static final int PAGE_SIZE = 20;

    // 管理员用户默认密码
    public static final String DEFAULT_PASSWORD = "123456";

}
