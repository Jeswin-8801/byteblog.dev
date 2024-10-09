package com.jeswin8801.byteBlog.controller;

import com.jeswin8801.byteBlog.entities.dto.blog.PostBlogRequestDto;
import com.jeswin8801.byteBlog.service.webapp.blog.abstracts.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/get-all-tags")
    public ResponseEntity<?> getAllTags() {
        return blogService.getAllTags().getResponseEntity();
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addNewBlog(@RequestPart("blog") PostBlogRequestDto postBlogRequestDto,
                                        @RequestPart("markdown") MultipartFile markdownFile,
                                        @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return blogService
                .addNewBlog(postBlogRequestDto, markdownFile, images)
                .getResponseEntity();
    }
}
