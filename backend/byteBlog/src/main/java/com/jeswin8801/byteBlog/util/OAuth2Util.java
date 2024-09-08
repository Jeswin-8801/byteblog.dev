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

    /**
     * Populate OAuth2UserInfo for specific OAuthProvider
     */
    public OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                            Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.getProvider()))
            return new GoogleOAuth2UserInfo(attributes);
        else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.getProvider()))
            return new GithubOAuth2UserInfo(attributes);
        else
            throw new InternalAuthenticationServiceException("Login with " + registrationId + " is not supported.");
    }
}
