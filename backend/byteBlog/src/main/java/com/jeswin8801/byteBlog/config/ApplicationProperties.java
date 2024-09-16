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

    private String issuer;

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
        private String destinationUrl;
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
        private String accessTokenSecret, refreshTokenSecret;
        private boolean isSecretKeyBase64Encoded = false;
        private long accessTokenDurationMillis = 43200000L; // 1/2 day
        private long refreshTokenDurationMillis = 86400000L; // 1 day
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
