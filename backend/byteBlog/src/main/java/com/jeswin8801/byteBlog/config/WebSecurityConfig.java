package com.jeswin8801.byteBlog.config;

import com.jeswin8801.byteBlog.security.AuthenticationEntryPointImpl;
import com.jeswin8801.byteBlog.security.entity.UserDetailsServiceImpl;
import com.jeswin8801.byteBlog.security.jwt.JWTAuthenticationFilter;
import com.jeswin8801.byteBlog.security.oauth2.OAuth2UserService;
import com.jeswin8801.byteBlog.security.oauth2.dao.HttpCookieOAuth2AuthorizationRequestRepository;
import com.jeswin8801.byteBlog.security.oauth2.dao.OAuth2AuthenticationFailureHandler;
import com.jeswin8801.byteBlog.security.oauth2.dao.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({ApplicationProperties.class})
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig {

    private final ApplicationProperties properties;

    // UserDetailsServiceImpl - To process custom user SignUp/SignIn request
    // OAuth2UserService - To process OAuth user SignUp/SignIn request
    private final UserDetailsServiceImpl userDetailsService;
    private final OAuth2UserService oAuth2UserService;

    // AuthenticationEntryPointImpl - Unauthorized Access handler
    // JWTAuthenticationFilter - Retrieves request JWT token and, validate and set Authentication
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    // Cookie based repository, OAuth2 Success and Failure Handler
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .cors((cors) -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/auth/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login((oauth2) -> oauth2
                        .authorizationEndpoint((authorizationEndpointConfig) -> authorizationEndpointConfig
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                        )
                        .redirectionEndpoint((redirectionEndpointConfig) -> redirectionEndpointConfig
                                .baseUri("/oauth2/callback/*")
                        )
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(oAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                );

        // Add our custom Token based authentication filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                        properties.getCors().getAllowedOrigins()
                )
        );
        configuration.setAllowedMethods(
                Arrays.asList(
                        properties.getCors().getAllowedMethods()
                )
        );
        configuration.setAllowedHeaders(
                Arrays.asList(
                        properties.getCors().getAllowedHeaders()
                )
        );
        configuration.setExposedHeaders(
                Arrays.asList(
                        properties.getCors().getExposedHeaders()
                )
        );

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
