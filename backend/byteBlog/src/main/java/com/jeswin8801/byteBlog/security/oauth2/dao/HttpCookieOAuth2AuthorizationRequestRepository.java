package com.jeswin8801.byteBlog.security.oauth2.dao;

import com.jeswin8801.byteBlog.util.AppUtil;
import com.jeswin8801.byteBlog.util.WebUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.jeswin8801.byteBlog.security.oauth2.enums.OauthCookieNames.*;

/**
 * Cookie based repository for storing Authorization requests with OAuth2 Authentication
 * <p>
 * By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
 * the authorization request. But, since our service is stateless, we can't save it in the session.
 * We'll use a cookie instead.
 */
@Slf4j
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    /**
     * Load authorization request from cookie
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {

        Assert.notNull(request, "Request cannot be null.");

        return WebUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME.getCookieName())
                .map(this::deserializeCookie)
                .orElse(null);
    }

    /**
     * Save authorization request in cookie
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        Assert.notNull(request, "Request cannot be null");
        Assert.notNull(response, "Response cannot be null");
        if (authorizationRequest == null) {

            WebUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME.getCookieName());
            WebUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME.getCookieName());
            WebUtil.deleteCookie(request, response, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME.getCookieName());
            return;
        }

        // Setting up -> authorizationRequest COOKIE, redirectUri COOKIE and originalRequestUri COOKIE
        WebUtil.addCookie(
                response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME.getCookieName(),
                AppUtil.serialize(authorizationRequest)
        );

        String redirectUri = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME.getCookieName());
        String originalRequestUri = request.getParameter(ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME.getCookieName());
        if (StringUtils.hasText(redirectUri))
            WebUtil.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME.getCookieName(), redirectUri);
        if (StringUtils.hasText(originalRequestUri))
            WebUtil.addCookie(response, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME.getCookieName(), originalRequestUri);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {

        OAuth2AuthorizationRequest originalRequest = loadAuthorizationRequest(request);
        WebUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME.getCookieName());
        return originalRequest;
    }

    private OAuth2AuthorizationRequest deserializeCookie(Cookie cookie) {
        return AppUtil.deserialize(cookie.getValue(), OAuth2AuthorizationRequest.class);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request,
                                                  HttpServletResponse response) {
        WebUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME.getCookieName());
        WebUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME.getCookieName());
        WebUtil.deleteCookie(request, response, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME.getCookieName());
    }
}
