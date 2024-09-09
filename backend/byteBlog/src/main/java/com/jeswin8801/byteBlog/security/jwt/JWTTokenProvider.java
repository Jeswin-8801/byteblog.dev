package com.jeswin8801.byteBlog.security.jwt;

import com.jeswin8801.byteBlog.config.ApplicationProperties;
import com.jeswin8801.byteBlog.entities.converters.UserMapper;
import com.jeswin8801.byteBlog.entities.dto.auth.JwtClaimsUserInfoDto;
import com.jeswin8801.byteBlog.entities.dto.user.AccessTokenClaimsUserDto;
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
    private Key accessTokenSecret;
    private long accessTokenDurationMillis;

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private UserMapper<AccessTokenClaimsUserDto> accessTokenClaimsUserDtoUserMapper;

    @Autowired
    private UserMapper<JwtClaimsUserInfoDto> jwtClaimsUserInfoDtoUserMapper;

    @PostConstruct
    protected void init() {

        accessTokenSecret = convertStringSecretToKey(
                properties.getJwt().getAccessTokenSecret()
        );

        accessTokenDurationMillis = properties.getJwt().getAccessTokenDurationMillis();
    }

    public String generateJWTAccessToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Claims claims = Jwts.claims()
                .add(
                        new HashMap<>() {{
                            put("user",
                                    jwtClaimsUserInfoDtoUserMapper.toDto(userPrincipal.getUser(), JwtClaimsUserInfoDto.class)
                            );
                            put("authorities", userPrincipal.getAuthorities());
                            put("attributes", userPrincipal.getAttributes());
                        }}
                )
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenDurationMillis);

        String token = Jwts.builder()
                .subject(userPrincipal.getUser().getId())
                .issuer(properties.getIssuer())
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(accessTokenSecret)
                .compact();

        log.info("token: {}\ntoken payload: {}", token, claims.toString());

        return token;
    }

    public Authentication getAuthenticationFromToken(String token) {

        Claims body = Jwts.parser()
                .verifyWith((SecretKey) accessTokenSecret).build()
                .parse(token).accept(Jws.CLAIMS)
                .getPayload();

        // Parsing Claims Data
        AccessTokenClaimsUserDto userDto = AppUtil.fromJson(body.get("user").toString(), AccessTokenClaimsUserDto.class);
        User userEntity = accessTokenClaimsUserDtoUserMapper.toEntity(userDto);

        Set<String> authoritiesSet = AppUtil.fromJson(body.get("authorities").toString(), (Class<Set<String>>) (Class<?>) Set.class);
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityUtil.convertRolesSetToGrantedAuthorityList(
                SecurityUtil.setOfStringToSetOfRoles(authoritiesSet)
        );
        Map<String, Object> attributes = AppUtil.fromJson(body.get("attributes").toString(), (Class<Map<String, Object>>) (Class<?>) Map.class);

        // Setting Principle Object

        UserDetailsImpl userDetails = UserDetailsImpl.build(userEntity, grantedAuthorities, attributes);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
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
                    .verifyWith((SecretKey) accessTokenSecret)
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

    private Key convertStringSecretToKey(String secretKey) {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(
                        properties.getJwt().isSecretKeyBase64Encoded() ?
                                secretKey :
                                Base64.getEncoder().encodeToString(secretKey.getBytes())
                )
        );
    }

}
