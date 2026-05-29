package com.tech.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

public class ServletUtil {
    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前请求实例
     *
     * @return 当前请求实例
     */
    public static HttpServletRequest getRequest() {
        return Objects.requireNonNull(getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {
        try {
            ServletRequestAttributes att;
            return ((att = getRequestAttributes()) != null) ? att.getResponse() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getIp() {
        HttpServletRequest request = getRequest();
        // nginx 中需要设置相关配置
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
