package com.jeswin8801.byteBlog.security.util;

import com.jeswin8801.byteBlog.entities.model.Role;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.UserPrivilege;
import com.jeswin8801.byteBlog.security.entity.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class SecurityUtil {

    public final String ROLE_DEFAULT = "ROLE_USER";

    /**
     * Converts list of roles into Collection of GrantedAuthority
     */
    public Collection<? extends GrantedAuthority> convertRolesToGrantedAuthorityList(Set<String> roles) {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        if (ObjectUtils.isEmpty(roles))
                return authorities;
        for (String role : roles) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
            authorities.add(grantedAuthority);
        }
        return authorities;
    }

    /**
     * Converts Collection of GrantedAuthority into list of roles
     */
    public Set<Role> convertGrantedAuthorityListToRolesSet(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(authority -> new Role(Enum.valueOf(UserPrivilege.class, authority.getAuthority())))
                .collect(Collectors.toSet());
    }

    public Set<String> setOfRolesToSetOfString(Set<Role> roles) {
        return new HashSet<>() {{
            for (Role role : roles)
                add(role.getPrivilege().name());
        }};
    }

    public Set<String> setOfStringAuthoritiesToSetOfRoles(Set<Map<String, String>> roles) {
        return new HashSet<>() {{
            for (Map<String, String> role : roles)
                add(role.get("authority"));
        }};
    }

    /**
     * Get Authentication object from SecurityContextHolder
     */
    public Authentication getAuthenticationObject() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Get current user principle
     */
    public UserDetailsImpl getCurrentUserPrinciple() {
        Authentication authentication = getAuthenticationObject();

        if (!Objects.isNull(authentication)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl)
                return ((UserDetailsImpl) principal);
        }
        return null;
    }

    /**
     * Get current user id
     */
    public Optional<String> getCurrentUserId() {
        return Optional.ofNullable(getCurrentUserPrinciple())
                .map(UserDetailsImpl::getUser)
                .map(User::getId);
    }


    /**
     * Check if user is Authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = getAuthenticationObject();
        if (!Objects.isNull(authentication)) {
            return authentication.getAuthorities().stream()
                    .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_DEFAULT));
        }
        return false;
    }
}
