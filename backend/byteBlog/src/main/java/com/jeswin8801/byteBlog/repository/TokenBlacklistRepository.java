package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.RefreshTokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<RefreshTokenBlacklist, Long> {
    Optional<RefreshTokenBlacklist> findByRefreshToken(String refreshToken);

    boolean existsByRefreshToken(String refreshToken);

    @Modifying
    void deleteByRefreshToken(String refreshToken);
}
