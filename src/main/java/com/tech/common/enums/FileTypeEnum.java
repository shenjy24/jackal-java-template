package com.tech.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * FileTypeEnum
 *
 * @author shenjy
 * @since 2023/10/23 10:10
 */
@Getter
public enum FileTypeEnum {
    IMAGE(1, "image", "图片"),
    AUDIO(2, "audio", "音频"),
    VIDEO(3, "video", "视频"),
    ;

    private final Integer code;

    private final String prefix;

    private final String message;

    private static final Map<Integer, FileTypeEnum> map = new HashMap<>();

    static {
        for (FileTypeEnum fileTypeEnum : values()) {
            map.put(fileTypeEnum.getCode(), fileTypeEnum);
        }
    }

    FileTypeEnum(Integer code, String prefix, String message) {
        this.code = code;
        this.prefix = prefix;
        this.message = message;
    }

    public static FileTypeEnum getEnum(Integer fileType) {
        return map.get(fileType);
    }
}
