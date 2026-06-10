package com.tech.config.interceptor;

import com.tech.common.annotation.auth.Anonymous;
import com.tech.common.annotation.auth.SemiAnonymous;
import com.tech.common.constant.Constants;
import com.tech.config.response.bean.BizException;
import com.tech.config.response.bean.SystemCode;
import com.tech.repository.entity.user.UserTokenEntity;
import com.tech.service.user.UserCommandService;
import com.tech.service.user.UserQueryService;
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
public class UserLoginInterceptor implements HandlerInterceptor {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

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
        UserTokenEntity userToken = userQueryService.getUserTokenByToken(token);
        if (userQueryService.isExpiredToken(userToken)) {
            if (semiAnonymous) {
                return true;
            }
            throw new BizException(SystemCode.NO_LOGIN);
        }
        long remainingMs = TimeUtil.toMillis(userToken.getExpireTime()) - System.currentTimeMillis();
        if (remainingMs < Constants.TOKEN_REFRESH_MS) {
            userToken.setExpireTime(TimeUtil.tokenExpireTime());
            userCommandService.updateUserToken(userToken);
        }
        request.setAttribute(Constants.REQ_ATT_USER, userToken.getUserId());
        return true;
    }

    private boolean hasAnnotation(HandlerMethod method, Class<? extends Annotation> annotationClass) {
        return method.hasMethodAnnotation(annotationClass) ||
                method.getBeanType().isAnnotationPresent(annotationClass);
    }
}
