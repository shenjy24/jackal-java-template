package com.tech.config.response;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tech.config.response.bean.BizException;
import com.tech.config.response.bean.CodeStatus;
import com.tech.config.response.bean.JsonResult;
import com.tech.config.response.bean.SystemCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

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
            CodeStatus codeStatus = e.getCodeStatus();
            JsonResult<Void> result = new JsonResult<>(codeStatus.getCode(), codeStatus.getMessage());
            log.warn("业务异常, method={}, uri={}, code={}, message={}",
                    request.getMethod(), request.getRequestURI(), result.getCode(), codeStatus.getMessage());
            return result;
        }
        if (ex instanceof ConstraintViolationException e) {
            String message = firstConstraintViolationMessage(e.getConstraintViolations());
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
            String message = StringUtils.defaultIfBlank(e.getMessage(), SystemCode.SERVER_ERROR.getMessage());
            JsonResult<Void> jsonResult = new JsonResult<>(SystemCode.SERVER_ERROR.getCode(), message);
            log.warn("请求体参数校验失败, method={}, uri={}, message={}",
                    request.getMethod(), request.getRequestURI(), message);
            return jsonResult;
        }
        if (ex instanceof NoResourceFoundException e) {
            JsonResult<Void> jsonResult = new JsonResult<>(String.valueOf(HttpStatus.SC_NOT_FOUND), "Not Found");
            log.debug("静态资源不存在, method={}, uri={}, message={}",
                    request.getMethod(), request.getRequestURI(), e.getMessage());
            return jsonResult;
        }
        log.error("未处理服务异常, method={}, uri={}", request.getMethod(), request.getRequestURI(), ex);
        return new JsonResult<>(SystemCode.SERVER_ERROR);
    }

    private String firstConstraintViolationMessage(Set<ConstraintViolation<?>> violations) {
        if (CollectionUtils.isEmpty(violations)) {
            return SystemCode.SERVER_ERROR.getMessage();
        }
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .filter(message -> message != null && !message.isBlank())
                .findFirst()
                .orElse(SystemCode.SERVER_ERROR.getMessage());
    }
}
