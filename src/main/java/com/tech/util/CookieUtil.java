package com.tech.util;

import com.tech.common.constant.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Objects;

public class CookieUtil {

    /**
     * 设置 Cookie
     *
     * @param response    响应对象
     * @param cookieName  Cookie 名
     * @param cookieValue Cookie 值
     * @param maxAge      存活最大时间（秒）
     */
    public static void setCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 设置登录 Token Cookie
     *
     * @param token 登录 token
     */
    public static void setCookie(String token) {
        HttpServletResponse response = Objects.requireNonNull(ServletUtil.getResponse());
        setCookie(response, Constants.COOKIE_KEY_TOKEN, token, Constants.TOKEN_EXPIRED_MS / 1000);
    }

    /**
     * 删除 Cookie
     *
     * @param cookieName cookie 名
     */
    public static void removeCookie(String cookieName) {
        HttpServletResponse response = Objects.requireNonNull(ServletUtil.getResponse());
        removeCookie(response, cookieName);
    }

    public static void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "";
        }
        return Arrays.stream(cookies)
                .filter(c -> Constants.COOKIE_KEY_TOKEN.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }
}
