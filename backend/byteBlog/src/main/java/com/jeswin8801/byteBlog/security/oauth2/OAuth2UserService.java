package com.jeswin8801.byteBlog.security.oauth2;

import com.jeswin8801.byteBlog.entities.converters.UserMapper;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.Role;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import com.jeswin8801.byteBlog.entities.model.enums.UserPrivilege;
import com.jeswin8801.byteBlog.security.entity.UserDetailsImpl;
import com.jeswin8801.byteBlog.security.oauth2.providers.abstracts.OAuth2UserInfo;
import com.jeswin8801.byteBlog.util.SecurityUtil;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import com.jeswin8801.byteBlog.util.AppUtil;
import com.jeswin8801.byteBlog.util.OAuth2Util;
import com.jeswin8801.byteBlog.util.exceptions.ByteBlogException;
import com.jeswin8801.byteBlog.util.exceptions.enums.UserExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <ol>
 *     <li>After Users Agrees by clicking on consent screen (To allow our app to access users allowed resources)</li>
 *     <ul>
 *         <li>loadUser will be triggered for all OAuth2 providers - (GitHub, Google, Facebook, Custom Auth Provider etc.)</li>
 *     </ul>
 *     <li>Retrieve attributes, from <b><i>security.oauth2.core.user.OAuth2User</i></b> which consists of { name, email, imageUrl and other attributes }</li>
 *     <ul>
 *         <li>Each registrationId will have their own attributes key (eg. For the attribute profile_pic -> {google: picture}, {github: avatar_url}, etc.)</li>
 *         <li>And Map the attributes specific to the OAuth2 provider in the <b><i>OAuth2UserInfo</i></b> object</li>
 *     </ul>
 *     <li>Determine if it is a <b><i>[ New Sign Up ]</i></b> or <b><i>[ Existing Sign In ]</i></b></li>
 *     <ul>
 *         <li>Sign In (email will be present in the database)</li>
 *         <h5>OR</h5>
 *         <li>Sign Up ( if email not found, register user, and save email into db)</li>
 *     </ul>
 *     <li>Create Principle Object i.e. an object of class <b><i>UserDetailsImpl</i></b> that implements <b><i>OAuth2User</i></b></li>
 *     <ul>
 *         <Li>return <b><i>security.oauth2.core.user.OAuth2User</i></b> that will set Authentication object, ( similar to <b><i>UserDetailsServiceImpl</i></b> - method <b><i>loadUserByUsername()</i></b> )</Li>
 *     </ul>
 *     <li>On completion "processOAuth2User()" Flow Jumps to either <b><i>OAuth2AuthenticationSuccessHandler</i></b> or <b><i>OAuth2AuthenticationFailureHandler</i></b></li>
 * </ol>
 */
@Slf4j
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper<UserDto> userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest,
                                         OAuth2User user) {

        // Mapped OAuth2User to specific OAuth2UserInfo for that registration id
        // clientRegistrationId is also the provider id - (google, facebook, gitHub, or Custom Auth Provider - ( keyClock, okta, authServer etc.)
        String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2Util.getOAuth2UserInfo(clientRegistrationId, user.getAttributes());

        // Check if the email is provided by the OAuthProvider
        String userEmail = oAuth2UserInfo.getEmail();
        if (!StringUtils.hasText(userEmail))
            throw new InternalAuthenticationServiceException("Sorry, Couldn't retrieve your email from Provider " + clientRegistrationId + ". Email not available or Private by default");

        UserDto userDto = null;

        User userTemp = userService.findUserByEmail(userEmail);
        if (!ObjectUtils.isEmpty(userTemp))
            userDto = userMapper.toDto(userTemp, UserDto.class);

        // Check if an entry with the oauth user email exists in the db with provider LOCAL
        if (!ObjectUtils.isEmpty(userDto) && userDto.getAuthProvider().equals(AuthProvider.LOCAL.getProvider()))
            throw new ByteBlogException(UserExceptions.USER_EMAIL_NOT_AVAILABLE.getMessage(), HttpStatus.CONFLICT);

        // Determine whether this is [ Sign up ] or [ New Sign up ]
        // Sign Up (if the given email is not associated with another user account, register user, and save to db)
        if (ObjectUtils.isEmpty(userDto)) {
            userDto = registerNewOAuthUser(userRequest, oAuth2UserInfo);
            userDto.setId(userService.findUserByEmail(userEmail).getId()); // update the automatically added id field upon saving user entry
        } else if (userDto.getAuthProvider().equals(clientRegistrationId)) {
            // If account exists i.e. userDto is not null, Sign In
            // Below code gets executed regardless of Sign in or Sign up as it conforms to both flows

            updateExistingOAuthUserInfo(userDto, oAuth2UserInfo); // updates just the name and profile_pic associated with the External Account as they as subject to changes
        } else
            throw new InternalAuthenticationServiceException(
                    String.format("Sorry, this email is linked with \"%s\" account. Please use your \"%s\" account to login.",
                            userDto.getAuthProvider(),
                            userDto.getAuthProvider())
            );

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(user.getAuthorities());
        grantedAuthorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_DEFAULT));
        User userEntity = userMapper.toEntity(userDto);
        return UserDetailsImpl.build(userEntity, grantedAuthorities, user.getAttributes());
    }

    private UserDto registerNewOAuthUser(OAuth2UserRequest oAuth2UserRequest,
                                         OAuth2UserInfo userInfo) {

        UserDto userDTO = new UserDto();
        userDTO.setFullName(userInfo.getName());
        userDTO.setUsername(userInfo.getName().split(" ")[0] + "_" + AppUtil.generateUUID().substring(0, 4));
        userDTO.setEmail(userInfo.getEmail());
        userDTO.setProfileImageUrl(userInfo.getImageUrl());
        userDTO.setProfileImageUpdated(false);
        userDTO.setAuthProvider(
                oAuth2UserRequest
                        .getClientRegistration()
                        .getRegistrationId()
        );
        userDTO.setRegisteredProviderId(userInfo.getId());
        userDTO.setRoles(Set.of(
                        new Role(
                                UserPrivilege.valueOf(SecurityUtil.ROLE_DEFAULT)
                        )
                )
        );
        userDTO.setOnline(true);
        userDTO.setEmailVerified(true);

        log.info("Registering new OAuth user: {}", userDTO.getEmail());

        userService.createUser(userDTO);

        return userDTO;
    }

    private void updateExistingOAuthUserInfo(UserDto existingUserDTO,
                                             OAuth2UserInfo oAuth2UserInfo) {
        boolean update = false;
        if (!existingUserDTO.getFullName().equals(oAuth2UserInfo.getName())) {
            existingUserDTO.setFullName(oAuth2UserInfo.getName());
            update = true;
        }
        if (
                !StringUtils.hasText(existingUserDTO.getProfileImageUrl()) ||
                        !existingUserDTO.getProfileImageUrl().equals(oAuth2UserInfo.getImageUrl())
        ) {
            existingUserDTO.setProfileImageUrl(
                    StringUtils.hasText(existingUserDTO.getProfileImageUrl()) ? existingUserDTO.getProfileImageUrl() : oAuth2UserInfo.getImageUrl()
            );
            update = true;
        }

        if (update) {
            log.info("Found changes in OAuth user info (name | profile pic); updating user info in DB");
            userService.updateOauthUser(existingUserDTO);
        }
    }
}
