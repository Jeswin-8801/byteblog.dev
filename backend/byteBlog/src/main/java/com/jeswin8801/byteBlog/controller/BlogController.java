package com.jeswin8801.byteBlog.controller;

import com.jeswin8801.byteBlog.service.webapp.blog.abstracts.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/get-all-tags")
    public ResponseEntity<?> getAllTags() {
        return blogService.getAllTags().getResponseEntity();
    }
}
