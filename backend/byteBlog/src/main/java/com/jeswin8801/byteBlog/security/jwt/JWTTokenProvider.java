package com.jeswin8801.byteBlog.security.jwt;

import com.jeswin8801.byteBlog.config.ApplicationProperties;
import com.jeswin8801.byteBlog.entities.converters.UserMapper;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.security.entity.UserDetailsImpl;
import com.jeswin8801.byteBlog.security.util.SecurityUtil;
import com.jeswin8801.byteBlog.util.AppUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

/**
 * Util based class that generates JWT token, create Authentication Object from token string and Validates token string
 */
@Component
@Slf4j
public class JWTTokenProvider {
    private static final String HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_TOKEN_START = "Bearer ";

    // Initialized from configuration properties
    private Key secretKey;
    private long validityInMilliseconds;

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    protected void init() {

        secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(
                        properties.getJwt().isSecretKeyBase64Encoded() ?
                                properties.getJwt().getSecretKey() :
                                Base64.getEncoder().encodeToString(properties.getJwt().getSecretKey().getBytes())
                )
        );

        validityInMilliseconds = properties.getJwt().getExpirationMillis();
    }

    public String generateJWTToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        String authoritiesJson = AppUtil.toJson(userPrincipal.getAuthorities());
        String attributesJson = AppUtil.toJson(userPrincipal.getAttributes());
        String userJson = AppUtil.toJson(
                userMapper.toDto(
                        userPrincipal.getUser()
                )
        );

        Claims claims = Jwts.claims().build();
        claims.putAll(
                new HashMap<>() {{
                    claims.put("email", userPrincipal.getEmail());
                    claims.put("user", userJson);
                    claims.put("authorities", authoritiesJson);
                    claims.put("attributes", attributesJson);
                }}
        );

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(userPrincipal.getEmail())
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    public Authentication getAuthenticationFromToken(String token) {

        Claims body = Jwts.parser()
                .verifyWith((SecretKey) secretKey).build()
                .parse(token).accept(Jws.CLAIMS)
                .getPayload();

        // Parsing Claims Data
        UserDto userDto = AppUtil.fromJson(body.get("user").toString(), UserDto.class);
        User userEntity = userMapper.toEntity(userDto);

        Set<String> authoritiesSet = AppUtil.fromJson(body.get("authorities").toString(), (Class<Set<String>>) (Class<?>) Set.class);
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityUtil.convertRolesSetToGrantedAuthorityList(
                SecurityUtil.setOfStringToSetOfRoles(authoritiesSet)
        );
        Map<String, Object> attributes = AppUtil.fromJson(body.get("attributes").toString(), (Class<Map<String, Object>>) (Class<?>) Map.class);

        // Setting Principle Object

        UserDetailsImpl userDetails = UserDetailsImpl.build(userEntity, grantedAuthorities, attributes);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    public String getBearerTokenFromRequestHeader(HttpServletRequest request) {

        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_START))
            return bearerToken.substring(BEARER_TOKEN_START.length());

        return null;
    }

    public boolean validateJWTToken(String token) {

        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);

            // returns false if the token expiry is before current date else true
            return !claims.getPayload().getExpiration().before(new Date());

        } catch (SignatureException exception) {
            log.error("Invalid JWT signature trace: ", exception);
        } catch (MalformedJwtException exception) {
            log.error("Invalid JWT token trace: ", exception);
        } catch (ExpiredJwtException exception) {
            log.error("Expired JWT token trace: ", exception);
        } catch (UnsupportedJwtException exception) {
            log.error("Unsupported JWT token trace: ", exception);
        } catch (IllegalArgumentException exception) {
            log.error("JWT token compact of handler are invalid trace: ", exception);
        }
        return false;
    }

}
