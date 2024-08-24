package com.jeswin8801.byteBlog.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WebUtil {
    private static int cookieExpireSeconds;

    public WebUtil(int cookieExpireSeconds) {
        WebUtil.cookieExpireSeconds = cookieExpireSeconds;
    }

    /**
     * Fetches a cookie from request
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request,
                                             String cookieKey) {

        List<Cookie> cookies = Arrays.asList(request.getCookies());

        if (!cookies.isEmpty())
            for (Cookie cookie : cookies)
                if (cookie.getName().equals(cookieKey))
                    return Optional.of(cookie);

        return Optional.empty();
    }

    /**
     * Utility for adding cookie
     */
    public static void addCookie(HttpServletResponse response,
                                 String cookieKey,
                                 String cookieValue) {

        if (StringUtils.hasText(cookieKey) && StringUtils.hasText(cookieValue)) {
            Cookie cookie = new Cookie(cookieKey, cookieValue);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(cookieExpireSeconds);
            response.addCookie(cookie);
        }
    }

    /**
     * Utility for deleting cookie
     */
    public static void deleteCookie(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String cookieKey) {

        List<Cookie> cookies = Arrays.asList(request.getCookies());
        if (!cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieKey)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
