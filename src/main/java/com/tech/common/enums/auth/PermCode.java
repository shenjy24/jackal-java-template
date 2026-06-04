package com.tech.common.enums.auth;

import lombok.Getter;

/**
 * 权限编码
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-02
 */
@Getter
public enum PermCode {
    USER_SAVE("auth:user:save", "新增用户"),
    USER_UPDATE("auth:user:update", "更新用户"),
    ;

    private final String code;
    private final String message;

    PermCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
