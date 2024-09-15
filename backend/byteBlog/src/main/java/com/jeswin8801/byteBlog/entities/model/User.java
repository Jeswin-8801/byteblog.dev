package com.jeswin8801.byteBlog.entities.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.time.Instant;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "username")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(name = "full_name")
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

    @Column(name = "is_profile_img_updated")
    private boolean isProfileImageUpdated;

    @Column(name = "is_online")
    private boolean isOnline;

    private String about;

    @Column(name = "auth_provider")
    private String authProvider;

    @Column(name = "registered_provider_id")
    private String registeredProviderId;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Role> roles;

    // Will be using same verificationCode and verificationCodeExpiresAt for both (email-verification and password reset)
    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_code_expires_at")
    private Instant verificationCodeExpiresAt;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;

    @UpdateTimestamp
    @Column(name = "last_updated_on", nullable = false)
    private Instant lastUpdatedOn;

    @OneToOne(
            mappedBy = "user",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private RefreshToken refreshToken;
}
