package com.jeswin8801.byteBlog.security.oauth2.dao;

import com.jeswin8801.byteBlog.config.ApplicationProperties;
import com.jeswin8801.byteBlog.security.jwt.JWTTokenProvider;
import com.jeswin8801.byteBlog.util.WebUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import static com.jeswin8801.byteBlog.security.oauth2.enums.OauthCookieNames.ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME;
import static com.jeswin8801.byteBlog.security.oauth2.enums.OauthCookieNames.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * <ol>
 *      <li>
 *          Flow comes here <b><i>"onAuthenticationSuccess()"</i></b>, After successful OAuth2 Authentication in <b><i>OAuth2UserService.class</i></b>
 *      </li>
 *      <ul>
 *          <li>
 *             We create Custom JWT Token and respond back to <b><i>redirect_uri</i></b>
 *          </li>
 *          <li>
 *             We validate the <b><i>redirect_uri</i></b> for security measures, to send token to only our authorized redirect origins
 *          </li>
 *      </ul>
 * <p>
 *      <li>
 *          By default, OAuth2 uses Session based <b><i>AuthorizationRequestRepository</i></b>, since we are using Cookie based <b><i>AuthorizationRequestRepository</i></b>
 *      </li>
 *      <ul>
 *          <li>
 *              We clear <b><i>"authorizationRequest"</i></b> stored in our cookie, before sending redirect response
 *          </li>
 *      </ul>
 * </ol>
 */
@Slf4j
@Service
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    private ApplicationProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @SneakyThrows
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = WebUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME.name())
                .map(Cookie::getValue);
        Optional<String> originalRequestUri = WebUtil.getCookie(request, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME.name())
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isRedirectOriginAuthorized(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String token = jwtTokenProvider.generateJWTAccessToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam(ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME.name(), originalRequestUri)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request,
                                                 HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isRedirectOriginAuthorized(String uri) {
        URI clientRedirectUri = URI.create(uri);

        log.info("clientRedirectUri: {}", clientRedirectUri);

        return Arrays.stream(appProperties.getOAuth2().getAuthorizedRedirectOrigins())
                .anyMatch(authorizedRedirectOrigin -> {
                    URI authorizedURI = URI.create(authorizedRedirectOrigin);
                    log.info("authorizedURI: {}", authorizedURI);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
