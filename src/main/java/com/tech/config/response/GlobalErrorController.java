package com.tech.config.response;

import com.tech.config.response.bean.BizException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 自定义错误处理器，处理404等异常信息
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-01-06
 */
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class GlobalErrorController extends BasicErrorController {

    public GlobalErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);

        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        // 将 404 等异常封装为内部异常
        throw new BizException(String.valueOf(status.value()), (String) body.get("error"));
    }
}
