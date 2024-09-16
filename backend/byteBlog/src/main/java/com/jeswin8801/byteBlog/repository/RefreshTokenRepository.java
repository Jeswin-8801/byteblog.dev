package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.RefreshToken;
import com.jeswin8801.byteBlog.entities.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RefreshToken r SET r.refreshToken = :refresh_token WHERE r.user = :user")
    void updateRefreshToken(@Param("refresh_token") String refreshToken,
                            @Param("user") User user);

    boolean existsByUser(User user);

    RefreshToken findByUser(User user);
}
