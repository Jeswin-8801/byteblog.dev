package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.RefreshTokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<RefreshTokenBlacklist, Long> {
    Optional<RefreshTokenBlacklist> findByRefreshToken(String refreshToken);

    boolean existsByRefreshToken(String refreshToken);

    void deleteByExpiryLessThan(Instant now);
}
