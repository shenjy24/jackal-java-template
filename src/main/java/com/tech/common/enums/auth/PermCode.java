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
    USER_QUERY("auth:user:query", "查询用户"),
    USER_SAVE("auth:user:save", "新增用户"),
    USER_UPDATE("auth:user:update", "更新用户"),
    USER_DELETE("auth:user:delete", "删除用户"),
    USER_RESET("auth:user:reset", "重置密码"),
    ROLE_QUERY("auth:role:query", "查询角色"),
    ROLE_SAVE("auth:role:save", "新增角色"),
    ROLE_UPDATE("auth:role:update", "更新角色"),
    ROLE_DELETE("auth:role:delete", "删除角色"),
    PERM_QUERY("auth:perm:query", "查询权限"),
    PERM_SAVE("auth:perm:save", "新增权限"),
    PERM_UPDATE("auth:perm:update", "更新权限"),
    PERM_DELETE("auth:perm:delete", "删除权限"),
    ;

    private final String code;
    private final String message;

    PermCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
