package com.jeswin8801.byteBlog.service.webapp.blog.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.blog.PostBlogRequestDto;
import com.jeswin8801.byteBlog.entities.dto.blog.TagsDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogService {
    GenericResponseDto<TagsDto> getAllTags();

    GenericResponseDto<MessageResponseDto> addNewBlog(
            PostBlogRequestDto postBlogRequestDto,
            MultipartFile markdownFile,
            List<MultipartFile> images
    );
}
