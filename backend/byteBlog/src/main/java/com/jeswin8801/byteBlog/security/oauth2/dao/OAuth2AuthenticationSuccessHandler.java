package com.jeswin8801.byteBlog.security.oauth2.dao;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

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
@Service
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

}
