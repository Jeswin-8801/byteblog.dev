package com.jeswin8801.byteBlog.repository;

import com.jeswin8801.byteBlog.entities.model.Comment;
import com.jeswin8801.byteBlog.entities.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {

    /**
     * Finds all comments authored by given User
     */
    List<Comment> findByUser(User user);
}
