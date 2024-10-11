package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.Blog;
import com.jeswin8801.byteBlog.entities.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, String> {

    /**
     * Checks for if a blog exists with the given heading
     */
    boolean existsByHeadingUri(String headingUri);

    /**
     * Find all Blog entries where the blog was authored by the given User
     */
    List<Blog> findByUser(User user);

    /**
     * Find Blog by Heading
     */
    Blog findByHeadingUri(String headingUri);
}
