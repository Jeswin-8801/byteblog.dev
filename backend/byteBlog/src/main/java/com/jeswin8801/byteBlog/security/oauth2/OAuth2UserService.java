package com.jeswin8801.byteBlog.security.oauth2;

import com.jeswin8801.byteBlog.entities.converters.UserMapper;
import com.jeswin8801.byteBlog.entities.dto.UserDto;
import com.jeswin8801.byteBlog.entities.model.Role;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import com.jeswin8801.byteBlog.entities.model.enums.UserPrivilege;
import com.jeswin8801.byteBlog.security.entity.UserDetailsImpl;
import com.jeswin8801.byteBlog.security.oauth2.providers.abstracts.OAuth2UserInfo;
import com.jeswin8801.byteBlog.security.util.SecurityUtil;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import com.jeswin8801.byteBlog.util.OAuth2Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest,
                                         OAuth2User user) {

        // Mapped OAuth2User to specific OAuth2UserInfo for that registration id
        // clientRegistrationId - (google, facebook, gitHub, or Custom Auth Provider - ( keyClock, okta, authServer etc.)
        String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2Util.getOAuth2UserInfo(clientRegistrationId, user.getAttributes());

        // Check if the email is provided by the OAuthProvider
        AuthProvider authProvider = AuthProvider.valueOf(clientRegistrationId.toLowerCase());
        String userEmail = oAuth2UserInfo.getEmail();
        if (!StringUtils.hasText(userEmail))
            throw new InternalAuthenticationServiceException("Sorry, Couldn't retrieve your email from Provider " + clientRegistrationId + ". Email not available or Private by default");

        // Determine is this [ Login ] or [ New Sign up ]
        // Sign In (email will be present in our database)  OR Sign Up ( if you don't have user email, we need to register user, and save email into db)
        Optional<UserDto> fetchedUser = userService.findUserByEmail(userEmail);
        if (fetchedUser.isEmpty())
            fetchedUser = Optional.of(registerNewOAuthUser(userRequest, oAuth2UserInfo));

        UserDto userDto = fetchedUser.get();
        if (userDto.getAuthProvider().equals(authProvider))
            updateExistingOAuthUser(userDto, oAuth2UserInfo);
        else
            throw new InternalAuthenticationServiceException(
                    String.format("Sorry, this email is linked with \"%s\" account. Please use your \"%s\" account to login.",
                            userDto.getAuthProvider().name(),
                            userDto.getAuthProvider().name())
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
        userDTO.setEmail(userInfo.getEmail());
        userDTO.setAuthProvider(
                AuthProvider.valueOf(
                        oAuth2UserRequest
                                .getClientRegistration()
                                .getRegistrationId().toUpperCase()
                )
        );
        userDTO.setRegisteredProviderId(userInfo.getId());
        userDTO.setRoles(Set.of(
                        new Role(
                                UserPrivilege.valueOf(SecurityUtil.ROLE_DEFAULT)
                        )
                )
        );
        userDTO.setEmailVerified(true);

        return userService.createUser(userDTO);
    }

    private void updateExistingOAuthUser(UserDto existingUserDTO,
                                         OAuth2UserInfo oAuth2UserInfo) {

        existingUserDTO.setFullName(oAuth2UserInfo.getName());
        existingUserDTO.setProfileImageUrl(oAuth2UserInfo.getImageUrl());

        BeanUtils.copyProperties(
                userService.updateUser(existingUserDTO),
                existingUserDTO
        );
    }
}
