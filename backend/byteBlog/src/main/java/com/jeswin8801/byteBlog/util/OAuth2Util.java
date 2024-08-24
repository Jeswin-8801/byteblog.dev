package com.jeswin8801.byteBlog.util;

import com.jeswin8801.byteBlog.security.oauth2.providers.GithubOAuth2UserInfo;
import com.jeswin8801.byteBlog.security.oauth2.providers.GoogleOAuth2UserInfo;
import com.jeswin8801.byteBlog.security.oauth2.providers.abstracts.OAuth2UserInfo;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import java.util.Map;

@UtilityClass
public class OAuth2Util {
    public final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    /**
     * UI-App/Web-Client will use this param to redirect flow to appropriate page
     */
    public final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public final String ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME = "original_request_uri";

    /**
     * Populate OAuth2UserInfo for specific OAuthProvider
     */
    public OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                            Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.name()))
            return new GoogleOAuth2UserInfo(attributes);
        else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.name()))
            return new GithubOAuth2UserInfo(attributes);
        else
            throw new InternalAuthenticationServiceException("Login with " + registrationId + " is not supported.");
    }
}
