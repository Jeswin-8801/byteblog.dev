package com.jeswin8801.byteBlog.entities.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeswin8801.byteBlog.entities.model.enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(name = "registered_provider_id")
    private String registeredProviderId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    // Will be using same verificationCode and verificationCodeExpiresAt for both (email-verification and password reset)
    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expires_at")
    private Instant verificationCodeExpiresAt;
}
