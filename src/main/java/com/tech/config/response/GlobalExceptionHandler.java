package com.tech.config.response;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.tech.config.response.bean.BizException;
import com.tech.config.response.bean.JsonResult;
import com.tech.config.response.bean.SystemCode;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
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
    public JsonResult handle(Exception ex) {
        if (ex instanceof BizException e) {
            Iterable<String> iterable = Splitter.on(":").trimResults().omitEmptyStrings().split(ex.getMessage());
            List<String> items = Lists.newArrayList(iterable);
            JsonResult<Void> result = new JsonResult<>(items.get(1), items.get(2));
            log.error("handle biz exception, {}, {}", result, e.getMessage());
            return result;
        }
        if (ex instanceof ConstraintViolationException e) {
            String message = e.getMessage().split(",")[0];
            message = message.split(":")[1].trim();
            JsonResult<Void> result = new JsonResult<>(SystemCode.SERVER_ERROR.getCode(), message);
            log.error("handle constraint violation exception, {}, {}", result, e.getMessage());
            return result;
        }
        if (ex instanceof MethodArgumentNotValidException e) {
            BindingResult result = e.getBindingResult();
            if (result.getFieldError() != null) {
                String msg = result.getFieldError().getDefaultMessage();
                JsonResult<Void> jsonResult = new JsonResult<>(SystemCode.SERVER_ERROR.getCode(), msg);
                log.error("handle method argument not valid exception, {}, {}", jsonResult, e.getMessage());
                return jsonResult;
            }
        }
        if (ex instanceof NoResourceFoundException e) {
            JsonResult<Void> jsonResult = new JsonResult<>(String.valueOf(HttpStatus.SC_NOT_FOUND), "Not Found");
            log.error("no resource found exception, {}, {}", jsonResult, e.getMessage());
            return jsonResult;
        }
        log.error("handle exception", ex);
        return new JsonResult<>(SystemCode.SERVER_ERROR);
    }
}
