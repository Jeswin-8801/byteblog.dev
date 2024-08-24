package com.jeswin8801.byteBlog.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "web")
@Data
@Slf4j
public class ApplicationProperties {

    public ApplicationProperties() {
        log.info("Application Properties Initialized");
    }

    // Mail config
    private Mail mail = new Mail();

    // CORS configuration
    private Cors cors = new Cors();

    // JWT token generation related properties
    private Jwt jwt = new Jwt();

    // Custom specific OAuth2 Properties
    private OAuth2 oAuth2 = new OAuth2();

    // Custom Defaults App/Web/Rest/Misc Properties
    private Defaults defaults = new Defaults();

    @Data
    public static class Mail {
        private String defaultEmailAddress;
        private long verificationCodeExpirationSeconds = 300;   // 5 minutes
    }

    @Data
    public static class Cors {
        private String[] allowedOrigins;
        private String[] allowedMethods;
        private String[] allowedHeaders;
        private String[] exposedHeaders;
    }

    @Data
    public static class Jwt {
        private String secretKey;
        private boolean isSecretKeyBase64Encoded = false;
        private long expirationMillis = 864000000L; // 10 days
        // For short-lived tokens and cookies
        private int shortLivedMillis = 120000; // Two minutes
    }

    @Data
    public static class OAuth2 {
        private String[] authorizedRedirectOrigins;
        private int cookieExpireSeconds = 120; // Two minutes
    }

    @Data
    public static class Defaults {
        private int defaultPageStart = 0;
        private int defaultPageSize = 50;
    }
}
