package com.tech.config.response.bean;

import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 通用返回结构
 *
 * @author shenjy
 * @since  2020/8/13
 */
@Getter
@Setter
@NoArgsConstructor
public class JsonResult<T> implements Serializable {
    private String code;
    private String message;
    private T data;

    public JsonResult(CodeStatus codeStatus) {
        this.code = codeStatus.getCode();
        this.message = codeStatus.getMessage();
    }

    public JsonResult(CodeStatus codeStatus, T data) {
        this.code = codeStatus.getCode();
        this.message = codeStatus.getMessage();
        this.data = data;
    }

    public JsonResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResult(String code, String message, T data) {
        this.code = code ;
        this.message =message ;
        this.data = data;
    }

    public static <T> JsonResult<T> success(T data) {
        return restResult(data, SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMessage());
    }

    private static <T> JsonResult<T> restResult(T data, String code, String msg) {
        JsonResult<T> result = new JsonResult();
        result.setCode(code);
        result.setData(data);
        result.setMessage(msg);
        return result;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
