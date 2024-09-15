package com.jeswin8801.byteBlog.entities.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeswin8801.byteBlog.entities.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;

    private String username;

    @JsonProperty("full-name")
    private String fullName;

    private String email;

    private String password;

    @JsonProperty("email-verified")
    private boolean emailVerified;

    @JsonProperty("profile-image-url")
    private String profileImageUrl;

    @JsonProperty("is-online")
    private boolean isOnline;

    @JsonProperty("is-profile-img-updated")
    private boolean isProfileImageUpdated;

    private String about;

    @JsonProperty("auth-provider")
    private String authProvider;

    @JsonProperty("registered-provider-id")
    private String registeredProviderId;

    private Set<Role> roles;

    @JsonProperty("verification-code")
    private String verificationCode;

    @JsonProperty("verification-code-expires-at")
    private Instant verificationCodeExpiresAt;
}