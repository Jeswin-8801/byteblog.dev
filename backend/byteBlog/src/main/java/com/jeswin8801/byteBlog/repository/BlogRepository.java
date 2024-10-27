package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.Blog;
import com.jeswin8801.byteBlog.entities.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

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

    /**
     * Retrieve all used tags
     */
    @Query("SELECT b.tags FROM Blog b")
    List<Set<String>> getAllUsedTags();

    /**
     * Find all blogs that use the given tag
     */
    @Query("SELECT b FROM Blog b WHERE :tag IN elements(b.tags)")
    List<Blog> findByTag(@Param("tag") String tag);

    /**
     * Retrieve all id's
     */
    @Query("SELECT b.id FROM Blog b")
    List<String> getAllIds();

    /**
     * Retrieve all the N latest blogs (N is the Pageable count)
     */
    @Query("SELECT b FROM Blog b ORDER BY b.createdOn DESC")
    List<Blog> findLatestBlogs(Pageable pageable);
}
