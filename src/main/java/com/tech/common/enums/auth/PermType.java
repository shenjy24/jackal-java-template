package com.tech.common.enums.auth;

import lombok.Getter;

@Getter
public enum PermType {
    API(1, "接口"),
    MENU(2, "菜单"),
    ;

    private final Integer code;
    private final String message;

    PermType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
