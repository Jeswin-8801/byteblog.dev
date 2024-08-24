package com.jeswin8801.byteBlog.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter just checks if the token is present in request Header, "Authorization" key
 * <p>
 * <ul>
 *     <li>If present -> Validates Token AND set Authentication Object into SecurityContextHolder</li>
 *     <li>Else       -> Continue filter chain</li>
 * </ul>
 */
@Service
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            String jwt = jwtTokenProvider.getBearerTokenFromRequestHeader(request);

            if (StringUtils.hasText(jwt) && this.jwtTokenProvider.validateJWTToken(jwt)) {
                Authentication authentication = this.jwtTokenProvider.getAuthenticationFromToken(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException exception) {

            log.info("Security exception Expired JWT token for user {} - {}", exception.getClaims().getSubject(), exception.getMessage());
            response.sendError(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value(), "Expired JWT token");

        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException exception) {

            log.info("Security exception {} ", exception.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        }
    }
}
