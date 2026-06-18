package com.tech.config.response;

import com.tech.config.response.bean.BizException;
import com.tech.config.response.bean.JsonResult;
import com.tech.config.response.bean.SystemCode;
import com.tech.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * 全局异常处理器
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-06
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public JsonResult handle(Exception ex, HttpServletRequest request) {
        if (ex instanceof BizException e) {
            List<String> items = StringUtil.split(ex.getMessage(), ":");
            JsonResult<Void> result = new JsonResult<>(items.get(1), items.get(2));
            log.warn("业务异常, method={}, uri={}, code={}, message={}",
                    request.getMethod(), request.getRequestURI(), result.getCode(), e.getMessage());
            log.error("handle biz exception, {}, {}", result, e.getMessage());
            return result;
        }
        if (ex instanceof ConstraintViolationException e) {
            String message = e.getMessage().split(",")[0];
            message = message.split(":")[1].trim();
            JsonResult<Void> result = new JsonResult<>(SystemCode.SERVER_ERROR.getCode(), message);
            log.warn("请求参数校验失败, method={}, uri={}, message={}",
                    request.getMethod(), request.getRequestURI(), e.getMessage());
            return result;
        }
        if (ex instanceof MethodArgumentNotValidException e) {
            BindingResult result = e.getBindingResult();
            if (result.getFieldError() != null) {
                String msg = result.getFieldError().getDefaultMessage();
                JsonResult<Void> jsonResult = new JsonResult<>(SystemCode.SERVER_ERROR.getCode(), msg);
                log.warn("请求体参数校验失败, method={}, uri={}, field={}, message={}",
                        request.getMethod(), request.getRequestURI(),
                        result.getFieldError().getField(), msg);
                return jsonResult;
            }
        }
        if (ex instanceof NoResourceFoundException e) {
            JsonResult<Void> jsonResult = new JsonResult<>(String.valueOf(HttpStatus.NOT_FOUND.value()), "Not Found");
            log.debug("静态资源不存在, method={}, uri={}, message={}",
                    request.getMethod(), request.getRequestURI(), e.getMessage());
            return jsonResult;
        }
        log.error("未处理服务异常, method={}, uri={}", request.getMethod(), request.getRequestURI(), ex);
        return new JsonResult<>(SystemCode.SERVER_ERROR);
    }
}
