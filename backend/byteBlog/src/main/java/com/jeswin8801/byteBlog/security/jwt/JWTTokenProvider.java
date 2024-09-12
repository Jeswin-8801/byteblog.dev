package com.jeswin8801.byteBlog.security.jwt;

import com.jeswin8801.byteBlog.config.ApplicationProperties;
import com.jeswin8801.byteBlog.entities.converters.UserMapper;
import com.jeswin8801.byteBlog.entities.dto.auth.JwtClaimsUserInfoDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.Role;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.repository.TokenBlacklistRepository;
import com.jeswin8801.byteBlog.repository.UserRepository;
import com.jeswin8801.byteBlog.security.entity.UserDetailsImpl;
import com.jeswin8801.byteBlog.security.util.SecurityUtil;
import com.jeswin8801.byteBlog.util.AppUtil;
import com.jeswin8801.byteBlog.util.exceptions.ByteBlogException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Util based class that generates JWT token, create Authentication Object from token string and Validates token string
 */
@Component
@Slf4j
public class JWTTokenProvider {
    private static final String BEARER_TOKEN_START = "Bearer ";

    // Initialized from configuration properties
    private Key accessTokenSecret;
    private long accessTokenDurationMillis;
    private Key refreshTokenSecret;
    private long refreshTokenDurationMillis;

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper<UserDto> userMapper;

    @Autowired
    private UserMapper<JwtClaimsUserInfoDto> jwtClaimsUserInfoDtoUserMapper;

    @PostConstruct
    protected void init() {

        accessTokenSecret = convertStringSecretToKey(
                properties.getJwt().getAccessTokenSecret()
        );
        refreshTokenSecret = convertStringSecretToKey(
                properties.getJwt().getRefreshTokenSecret()
        );

        accessTokenDurationMillis = properties.getJwt().getAccessTokenDurationMillis();
        refreshTokenDurationMillis = properties.getJwt().getRefreshTokenDurationMillis();
    }

    public String generateToken(Authentication authentication, TokenType tokenType) {

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
        Date validity = new Date(now.getTime() + (tokenType.equals(TokenType.ACCESS) ? accessTokenDurationMillis : refreshTokenDurationMillis));

        String token;
        if (tokenType.equals(TokenType.ACCESS))
            token = Jwts.builder()
                    .subject(userPrincipal.getUser().getEmail())
                    .issuer(properties.getIssuer())
                    .claims(claims)
                    .issuedAt(now)
                    .expiration(validity)
                    .signWith(accessTokenSecret)
                    .compact();
        else
            token = Jwts.builder()
                    .subject(userPrincipal.getUser().getEmail())
                    .issuedAt(now)
                    .expiration(validity)
                    .signWith(refreshTokenSecret)
                    .compact();


        log.info("{}: {}", tokenType.name(), token);

        return token;
    }

    public Authentication getAuthenticationFromToken(String token, TokenType tokenType) {

        Claims body = Jwts.parser()
                .verifyWith((SecretKey) (tokenType.equals(TokenType.ACCESS) ? accessTokenSecret : refreshTokenSecret)).build()
                .parse(token).accept(Jws.CLAIMS)
                .getPayload();

        UserDto userDto;

        if (tokenType.equals(TokenType.ACCESS)) {
            // Parsing Claims Data
            userDto = AppUtil.fromJson(AppUtil.toJson(body.get("user")), UserDto.class);
        } else {
            log.info(AppUtil.toJson((body)));
            userDto = UserDto.builder().email(body.getSubject()).build();
        }

        User userEntity = userMapper.toEntity(userDto);

        Set<Role> authoritiesSet;

        if (tokenType.equals(TokenType.ACCESS)) {
            authoritiesSet = SecurityUtil.setOfStringToSetOfRoles(
                    AppUtil.fromJson(
                            AppUtil.toJson(body.get("authorities")),
                            (Class<Set<Map<String, String>>>) (Class<?>) Set.class
                    )
            );
        } else {
            Optional<User> user = userRepository
                    .findByEmail(body.getSubject());
            if (user.isPresent()) {
                userEntity = user.get();
                authoritiesSet = user.get().getRoles();
            } else
                throw new ByteBlogException(
                        String.format("No user found with email: %s given in the subject of Refresh Token", body.getSubject()),
                        HttpStatus.BAD_REQUEST
                );
        }

        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityUtil.convertRolesSetToGrantedAuthorityList(authoritiesSet);
        Map<String, Object> attributes = AppUtil.fromJson(
                tokenType.equals(TokenType.ACCESS) ?
                        AppUtil.toJson(body.get("attributes")) : """
                        {
                            "attributes": {}
                        }
                        """,
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        // Setting Principle Object

        UserDetailsImpl userDetails = UserDetailsImpl.build(userEntity, grantedAuthorities, attributes);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    public String getSubjectFromToken(String token, TokenType tokenType) {
        Claims body = Jwts.parser()
                .verifyWith((SecretKey) (tokenType.equals(TokenType.ACCESS) ? accessTokenSecret : refreshTokenSecret)).build()
                .parse(token).accept(Jws.CLAIMS)
                .getPayload();

        return body.getSubject();
    }

    public Date getTokenExpiry(String token, TokenType tokenType) {
        Claims body = Jwts.parser()
                .verifyWith((SecretKey) (tokenType.equals(TokenType.ACCESS) ? accessTokenSecret : refreshTokenSecret)).build()
                .parse(token).accept(Jws.CLAIMS)
                .getPayload();

        return body.getExpiration();
    }

    public String getBearerTokenFromRequestHeader(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_START))
            return bearerToken.substring(BEARER_TOKEN_START.length());

        return null;
    }

    public boolean validateJWTToken(String token, TokenType tokenType) {

        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith((SecretKey) (tokenType.equals(TokenType.ACCESS) ? accessTokenSecret : refreshTokenSecret))
                    .build()
                    .parseSignedClaims(token);

            // returns false if the token expiry is before current date else true
            return !claims.getPayload().getExpiration().before(new Date());

        } catch (SignatureException | IllegalArgumentException | MalformedJwtException |
                 UnsupportedJwtException exception) {
            throw new ByteBlogException(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException exception) {
            if (tokenType.equals(TokenType.REFRESH)) {
                if (tokenBlacklistRepository.findByRefreshToken(token).isPresent())
                    tokenBlacklistRepository.deleteByRefreshToken(token);
                log.error("Refresh token expired {}", exception.getMessage());
                return false;
            }
            throw new ByteBlogException("Token Expired", HttpStatus.UNAUTHORIZED);
        }
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
