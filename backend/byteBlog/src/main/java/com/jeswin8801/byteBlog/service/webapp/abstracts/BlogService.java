package com.jeswin8801.byteBlog.service.webapp.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.blog.BlogDto;
import com.jeswin8801.byteBlog.entities.dto.blog.BlogsCompactResponseDto;
import com.jeswin8801.byteBlog.entities.dto.blog.PostBlogRequestDto;
import com.jeswin8801.byteBlog.entities.dto.blog.TagsDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface BlogService {
    GenericResponseDto<TagsDto> getAllTags();

    GenericResponseDto<MessageResponseDto> addNewBlog(
            PostBlogRequestDto postBlogRequestDto,
            MultipartFile markdownFile,
            List<MultipartFile> images
    );

    GenericResponseDto<Set<BlogsCompactResponseDto>> getAllBlogsByUserId(String userId);

    GenericResponseDto<BlogDto> getBlogByHeading(String heading);
}
