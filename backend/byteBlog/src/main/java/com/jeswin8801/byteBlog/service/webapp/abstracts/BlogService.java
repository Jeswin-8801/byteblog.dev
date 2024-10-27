package com.jeswin8801.byteBlog.service.webapp.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.blog.*;
import com.jeswin8801.byteBlog.entities.model.Blog;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BlogService {
    GenericResponseDto<TagsDto> getAllTags();

    GenericResponseDto<MessageResponseDto> addNewBlog(
            PostBlogRequestDto postBlogRequestDto,
            MultipartFile markdownFile,
            List<MultipartFile> images
    );

    GenericResponseDto<Set<BlogsCompactResponseDto>> getAllBlogsByAuthor(String username);

    GenericResponseDto<BlogDto> getBlogByHeading(String headingUri);

    GenericResponseDto<AuthorDto> getBlogAuthorDetails(String username);

    Blog getBlogById(String blogId);

    GenericResponseDto<Map<String, Integer>> getAllUsedTags();

    GenericResponseDto<Set<BlogsCompactResponseDto>> getAllBlogsByTag(String tag);

    GenericResponseDto<BlogsCompactResponseDto> getFeaturedBlog();

    GenericResponseDto<Set<BlogsCompactResponseDto>> getLatestBlogsAsPageable(int pageNumber, int pageSize);
}
