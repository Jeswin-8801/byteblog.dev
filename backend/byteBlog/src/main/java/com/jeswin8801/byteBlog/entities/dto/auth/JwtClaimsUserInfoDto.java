package com.jeswin8801.byteBlog.entities.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeswin8801.byteBlog.entities.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class JwtClaimsUserInfoDto {
    private String id;

    private String username;

    @JsonProperty("full-name")
    private String fullName;

    private String email;

    @JsonProperty("email-verified")
    private boolean emailVerified;

    @JsonProperty("profile-image-url")
    private String profileImageUrl;

    @JsonProperty("is-online")
    private boolean isOnline;

    @JsonProperty("auth-provider")
    private String authProvider;

    @JsonProperty("registered-provider-id")
    private String registeredProviderId;

    private Set<Role> roles;
}
