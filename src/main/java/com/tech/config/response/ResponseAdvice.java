package com.tech.config.response;

import com.tech.common.annotation.auth.DirectResponse;
import com.tech.config.response.bean.JsonResult;
import com.tech.config.response.bean.SystemCode;
import com.tech.util.JsonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 响应结果封装
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-06
 */
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = methodParameter.getMethod();
        return method == null || method.getAnnotation(DirectResponse.class) == null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        if (!(body instanceof JsonResult)) {
            if (!(body instanceof String)) {
                return new JsonResult(SystemCode.SUCCESS, body);
            } else {
                return JsonUtil.toMap(new JsonResult(SystemCode.SUCCESS, body));
            }
        }
        return body;
    }
}
