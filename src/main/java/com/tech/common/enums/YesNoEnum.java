package com.tech.common.enums;

import lombok.Getter;

/**
 * YesNoEnum
 *
 * @author shenjy
 * @since 2025-01-02
 * @version 1.0
 */
@Getter
public enum YesNoEnum {
    NO(0, "NO"),
    YES(1, "YES"),
    ;

    private final Integer code;

    private final String message;

    YesNoEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
