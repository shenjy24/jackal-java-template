package com.tech.common.enums;

import com.tech.config.response.bean.CodeStatus;

/**
 * ErrorCode
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-02
 */
public enum ErrorCode implements CodeStatus {
    PARAM_ERROR("100001", "参数异常"),
    DESERIALIZE_ERROR("100002", "序列化异常"),

    /**
     * 用户模块
     */
    USER_ERROR1("200001", "用户名重复"),
    USER_ERROR2("200002", "手机号重复"),
    USER_ERROR3("200003", "用户不存在"),
    USER_ERROR4("200004", "账号或密码错误"),
    USER_ERROR5("200005", "账号重复"),
    USER_ERROR6("200006", "创建账号异常"),
    USER_ERROR7("200007", "账号token不存在"),

    /**
     * 阿里云
     */
    ALIYUN_ERROR1("800001", "文件上传异常"),

    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
