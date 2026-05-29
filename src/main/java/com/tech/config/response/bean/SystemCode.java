package com.tech.config.response.bean;

/**
 * 系统状态码
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-02
 */
public enum SystemCode implements CodeStatus {
    SUCCESS("2000", "success"),
    NO_LOGIN("2001", "no login"),
    NO_PERM("2002", "no perm"),
    SERVER_ERROR("2003", "server error"),
    ;

    private final String code;
    private final String message;

    SystemCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
