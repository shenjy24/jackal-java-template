package com.tech.common.enums.auth;

import lombok.Getter;

/**
 * 权限类型
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-02
 */
@Getter
public enum PermType {
    DIRECTORY(1, "目录"),
    MENU(2, "菜单"),
    BUTTON(3, "按钮"),
    ;

    private final Integer code;
    private final String message;

    PermType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
