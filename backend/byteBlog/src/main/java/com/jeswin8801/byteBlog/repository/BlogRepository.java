package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, String> {

    /**
     * Checks for if a blog exists with the given heading
     */
    boolean existsByHeading(String heading);
}
