package com.tech.config.interceptor;

import com.tech.common.annotation.auth.Anonymous;
import com.tech.common.annotation.auth.SemiAnonymous;
import com.tech.common.constant.Constants;
import com.tech.config.response.bean.BizException;
import com.tech.config.response.bean.SystemCode;
import com.tech.repository.entity.auth.AdminUserTokenEntity;
import com.tech.service.auth.AuthCommandService;
import com.tech.service.auth.AuthQueryService;
import com.tech.util.CookieUtil;
import com.tech.util.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminLoginInterceptor implements HandlerInterceptor {

    private final AuthQueryService authQueryService;
    private final AuthCommandService authCommandService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (hasAnnotation(handlerMethod, Anonymous.class)) {
            return true;
        }
        String token = CookieUtil.getToken(request);
        boolean semiAnonymous = hasAnnotation(handlerMethod, SemiAnonymous.class);
        if (!semiAnonymous && StringUtils.isBlank(token)) {
            throw new BizException(SystemCode.NO_LOGIN);
        }
        if (semiAnonymous && StringUtils.isBlank(token)) {
            return true;
        }
        AdminUserTokenEntity adminToken = authQueryService.getTokenByToken(token);
        if (authQueryService.isExpiredToken(adminToken)) {
            if (semiAnonymous) {
                return true;
            }
            throw new BizException(SystemCode.NO_LOGIN);
        }
        long currentTime = System.currentTimeMillis();
        long expireTime = adminToken.getExpireTime().getTime();
        if (expireTime - currentTime < Constants.TOKEN_REFRESH_MS) {
            adminToken.setExpireTime(TimeUtil.tokenExpireTime());
            authCommandService.updateToken(adminToken);
        }
        request.setAttribute(Constants.REQ_ATT_USER, adminToken.getUserId());
        return true;
    }

    private boolean hasAnnotation(HandlerMethod method, Class<? extends Annotation> annotationClass) {
        return method.hasMethodAnnotation(annotationClass) ||
                method.getBeanType().isAnnotationPresent(annotationClass);
    }
}
