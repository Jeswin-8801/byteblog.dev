package com.jeswin8801.byteBlog.security.entity;

import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.security.util.SecurityUtil;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * If the authentication process is successful, we can get User’s information such as username, password, authorities from an Authentication object.
 * <p>
 * To get more data (id, email…), we create an implementation of the <b><i>UserDetails</i></b> interface.
 */
@Data
public class UserDetailsImpl implements OAuth2User, UserDetails {

    private String username;

    @Getter
    private String email;

    private String password;

    private User user;

    // refers to User -> Authorities, Usually defines roles (ROLE_USER, ROLE_ADMIN)
    // Converts Set<Role> into List<GrantedAuthority>. It is important to work with Spring Security and Authentication object later.
    private Collection<? extends GrantedAuthority> authorities;

    // permissions or combination of Scope:Permissions e.g. users:full, users:read, profile:full, profile:edit
    // private Map<String, String> permissions;
    // OAuth2 Provider attributes or custom Attributes
    private Map<String, Object> attributes;

    public UserDetailsImpl(String email,
                           String password,
                           User user,
                           Collection<? extends GrantedAuthority> authorities,
                           Map<String, Object> attributes) {
        this.username = email.split("@")[0];
        this.email = email;
        this.password = password;
        this.user = user;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    /**
     * Builds a <b><i>UserDetails</i></b> object from <b><i>User</i></b> model
     */
    public static UserDetailsImpl build(User user) {

        return new UserDetailsImpl(
                user.getEmail(),
                user.getPassword(),
                user,
                SecurityUtil
                        .convertRolesSetToGrantedAuthorityList(user.getRoles()),
                new HashMap<>()
        );
    }

    /**
     * Builds a <b><i>UserDetails</i></b> object from <b><i>authorities</i></b> and <b><i>attributes</i></b>
     * <p>
     * Overloaded method <b><i>build()</i></b>
     */
    public static UserDetailsImpl build(User user,
                                        Collection<? extends GrantedAuthority> authorities,
                                        Map<String, Object> attributes) {

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        userDetails.setAuthorities(authorities);
        userDetails.setAttributes(attributes);

        return userDetails;
    }

    // OAuth2User methods 👇

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return this.email;
    }

    // UserDetails methods 👇

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
