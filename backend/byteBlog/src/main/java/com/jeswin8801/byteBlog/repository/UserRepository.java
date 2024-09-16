package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * <h5>attributePaths = "roles" :</h5>
     * This specifies that the roles attribute of the <b><i>User</i></b> entity should be included in the entity graph.
     * This means that when the query is executed, the <b><i>roles</i></b> attribute will be eagerly loaded along with the <b><i>User</i></b> entity
     * <h5>type = EntityGraph.EntityGraphType.LOAD :</h5>
     * This specifies the type of entity graph. The LOAD type indicates that the specified attributes (in this case, roles) should be loaded eagerly,
     * while other attributes will follow their default fetch type.
     * <br/><br/>
     * <p>
     * This is done so that the <b><i>roles</i></b> data for that user can be accessed instantaneously in <b><i>UserDetailsImpl</i></b>
     */
    @EntityGraph(attributePaths = "roles", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByEmail(String email);

    /**
     * Checks for if a user exists with the given email
     */
    boolean existsByEmail(String email);

    /**
     * Checks for if a user exists with the given username
     */
    boolean existsByUsername(String username);

    /**
     * Updates password
     */
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void updatePassword(@Param("password") String password,
                        @Param("id") String id);

    /**
     * Find user by id and check if verification code matches as well as if the code has expired
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.email = :email " +
            "and u.verificationCodeExpiresAt >= current_timestamp and u.verificationCode = :verificationCode")
    Optional<User> verifyCodeAndRetrieveUser(@Param("email") String email,
                                             @Param("verificationCode") String verificationCode);
}
