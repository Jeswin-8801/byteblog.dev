package com.jeswin8801.byteBlog.service.auth;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.auth.AccessTokenDto;
import com.jeswin8801.byteBlog.entities.dto.auth.AuthResponseDto;
import com.jeswin8801.byteBlog.entities.model.RefreshToken;
import com.jeswin8801.byteBlog.entities.model.RefreshTokenBlacklist;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.repository.RefreshTokenRepository;
import com.jeswin8801.byteBlog.repository.TokenBlacklistRepository;
import com.jeswin8801.byteBlog.security.jwt.JWTTokenProvider;
import com.jeswin8801.byteBlog.security.jwt.TokenType;
import com.jeswin8801.byteBlog.service.auth.abstracts.RefreshTokenService;
import com.jeswin8801.byteBlog.util.exceptions.ByteBlogException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Transactional
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Scheduled(cron = "0 */5 * * * ?") // Run every 5 minutes
    public void purgeExpiredTokens() {
        Instant now = new Date().toInstant();
        tokenBlacklistRepository.deleteByExpiryLessThan(now);
    }

    @Override
    public AuthResponseDto constructAuthResponseForLogin(Authentication authentication, User user) {
        String refreshToken = jwtTokenProvider.generateToken(authentication, TokenType.REFRESH);

        this.processRefreshToken(refreshToken, user);

        return AuthResponseDto.builder()
                .accessToken(
                        jwtTokenProvider.generateToken(authentication, TokenType.ACCESS)
                )
                .refreshToken(
                        refreshToken
                )
                .build();
    }

    @Override
    public GenericResponseDto<AccessTokenDto> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.getBearerTokenFromRequestHeader(request);
        if (StringUtils.hasText(refreshToken)) {
            Optional<RefreshTokenBlacklist> refreshTokenBlacklisted = tokenBlacklistRepository.findByRefreshToken(refreshToken);
            if (refreshTokenBlacklisted.isPresent() &&
                    refreshTokenBlacklisted.get().getRefreshToken().equals(refreshToken)) {
                throw new ByteBlogException("This token has been blacklisted", HttpStatus.UNAUTHORIZED);
            }

            if (!jwtTokenProvider.validateJWTToken(refreshToken, TokenType.REFRESH))
                return GenericResponseDto.<AccessTokenDto>builder()
                        .message(null)
                        .httpStatusCode(HttpStatus.BAD_REQUEST)
                        .build();


            AccessTokenDto tokenDto = AccessTokenDto.builder()
                    .accessToken(
                            jwtTokenProvider.generateToken(
                                    jwtTokenProvider.getAuthenticationFromToken(
                                            refreshToken,
                                            TokenType.REFRESH
                                    ),
                                    TokenType.ACCESS
                            )
                    ).build();

            return GenericResponseDto.<AccessTokenDto>builder()
                    .message(tokenDto)
                    .httpStatusCode(HttpStatus.OK)
                    .build();
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @Override
    public void deleteRefreshTokenFromRefreshTokenTable(String refreshToken, User user) {
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .build();
        refreshTokenRepository.delete(refreshTokenEntity);
    }

    @Override
    public void processRefreshToken(String refreshToken, User user) {
        if (refreshTokenRepository.existsByUser(user)) {
            blacklistRefreshToken(
                    refreshTokenRepository.findByUser(user)
                            .getRefreshToken()
            );
            refreshTokenRepository.updateRefreshToken(refreshToken, user);
        } else {
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .refreshToken(refreshToken)
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);
        }
    }

    @Override
    public void blacklistRefreshToken(String refreshToken) {
        if (jwtTokenProvider.validateJWTToken(refreshToken, TokenType.REFRESH) &&
                !tokenBlacklistRepository.existsByRefreshToken(refreshToken)) { // checking for exists because after logging out, the refresh-token in the RefreshToken table is not removed even after adding to blacklist table
            tokenBlacklistRepository.save(
                    RefreshTokenBlacklist.builder()
                            .refreshToken(refreshToken)
                            .expiry(
                                    jwtTokenProvider.getTokenExpiry(refreshToken, TokenType.REFRESH).toInstant()
                            )
                            .build()
            );
        }
    }
}
