package com.jeswin8801.byteBlog.controller;

import com.jeswin8801.byteBlog.entities.dto.comment.PostCommentDto;
import com.jeswin8801.byteBlog.service.webapp.abstracts.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @PostMapping
    public ResponseEntity<?> postComment(@RequestBody PostCommentDto postCommentDto) {
        return commentsService.postComment(postCommentDto).getResponseEntity();
    }

    @GetMapping("/author")
    public ResponseEntity<?> getCommentsByUser(@RequestParam String username) {
        return commentsService.getCommentsByUser(username).getResponseEntity();
    }

    // unrestricted access
    @RestController
    @RequestMapping("/unrestricted/comments")
    public class UnrestrictedController {

        @GetMapping
        public ResponseEntity<?> getCommentsForBlog(@RequestParam("blog-id") String blogId) {
            return commentsService.getCommentsForBlog(blogId).getResponseEntity();
        }

    }
}
