package com.jeswin8801.byteBlog.service.webapp.blog;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.blog.PostBlogRequestDto;
import com.jeswin8801.byteBlog.entities.dto.blog.TagsDto;
import com.jeswin8801.byteBlog.entities.model.Blog;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.BlogTags;
import com.jeswin8801.byteBlog.repository.BlogRepository;
import com.jeswin8801.byteBlog.service.webapp.blog.abstracts.BlogService;
import com.jeswin8801.byteBlog.service.webapp.minio.FileStorageService;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import com.jeswin8801.byteBlog.util.exceptions.ResourceConflictException;
import com.jeswin8801.byteBlog.util.exceptions.enums.BlogExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public GenericResponseDto<TagsDto> getAllTags() {
        return GenericResponseDto.<TagsDto>builder()
                .message(
                        TagsDto.builder().tags(
                                Arrays.stream(BlogTags.values())
                                        .map(BlogTags::getTag)
                                        .collect(Collectors.toSet())
                        ).build()
                )
                .httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponseDto<MessageResponseDto> addNewBlog(PostBlogRequestDto postBlogRequestDto, MultipartFile markdownFile, List<MultipartFile> images) {
        User user = userService.findUserById(postBlogRequestDto.getUserId());

        log.info(String.valueOf(postBlogRequestDto));

        if (blogRepository.existsByHeading(postBlogRequestDto.getHeader()))
            throw new ResourceConflictException(BlogExceptions.BLOG_WITH_HEADER_ALREADY_EXISTS.getMessage());

        String markdownFileUrl = null;
        if (Objects.nonNull(markdownFile)) {
            try {
                markdownFileUrl = fileStorageService.uploadFile(markdownFile);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload Markdown File", e);
            }
        }

        Set<String> imageUrls = new HashSet<>();
        if (!CollectionUtils.isEmpty(images)) {
            for (MultipartFile image: images) {
                if (Objects.nonNull(image)) {
                    try {
                        imageUrls.add(fileStorageService.uploadFile(image));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload Image", e);
                    }
                }
            }
        }

        blogRepository.save(
                Blog.builder()
                        .heading(postBlogRequestDto.getHeader())
                        .description(postBlogRequestDto.getDescription())
                        .tags(postBlogRequestDto.getTags())
                        .primaryTag(postBlogRequestDto.getPrimaryTag())
                        .markdownFileUrl(markdownFileUrl)
                        .images(imageUrls)
                        .user(user)
                        .build()
        );

        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder()
                                .message("Successfully added Blog")
                                .build()
                ).httpStatusCode(HttpStatus.OK)
                .build();
    }
}
