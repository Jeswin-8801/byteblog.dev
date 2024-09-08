package com.jeswin8801.byteBlog.security.oauth2.dao;

import com.jeswin8801.byteBlog.util.AppUtil;
import com.jeswin8801.byteBlog.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

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

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");

        return Stream.of(request.getCookies())
                .filter(cookie ->
                        cookie.getName().equals(
                                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME.getCookieName()
                        )
                )
                .findFirst()
                .map(cookie ->
                        AppUtil.deserialize(cookie.getValue(), OAuth2AuthorizationRequest.class)
                )
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");

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
                AppUtil.serialize(authorizationRequest, OAuth2AuthorizationRequest.class)
        );

        String redirectUri = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME.getCookieName());
        if (StringUtils.hasText(redirectUri))
            WebUtil.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME.getCookieName(), redirectUri);

        String originalRequestUri = request.getParameter(ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME.getCookieName());
        if (StringUtils.hasText(originalRequestUri))
            WebUtil.addCookie(response, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME.getCookieName(), originalRequestUri);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");

        Stream.of(request.getCookies())
                .forEach(cookie -> cookie.setMaxAge(0));

        return loadAuthorizationRequest(request);
    }
}
