package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

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
}
