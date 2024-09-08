package com.jeswin8801.byteBlog.security.oauth2.enums;

import lombok.Getter;

@Getter
public enum OauthCookieNames {
    OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME("oauth2_auth_request"),

    /**
     * UI-App/Web-Client will use this param to redirect flow to appropriate page
     */
    REDIRECT_URI_PARAM_COOKIE_NAME("redirect_uri"),
    ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME("original_request_uri");

    private final String cookieName;

    OauthCookieNames(String cookieName) {
        this.cookieName = cookieName;
    }
}
