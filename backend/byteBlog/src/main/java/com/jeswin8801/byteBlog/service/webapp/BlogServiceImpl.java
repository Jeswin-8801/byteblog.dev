package com.jeswin8801.byteBlog.service.webapp;

import com.jeswin8801.byteBlog.entities.converters.BlogMapper;
import com.jeswin8801.byteBlog.entities.converters.CommentMapper;
import com.jeswin8801.byteBlog.entities.converters.UserMapper;
import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.blog.*;
import com.jeswin8801.byteBlog.entities.model.Blog;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.entities.model.enums.BlogTags;
import com.jeswin8801.byteBlog.repository.BlogRepository;
import com.jeswin8801.byteBlog.service.webapp.abstracts.BlogService;
import com.jeswin8801.byteBlog.service.webapp.abstracts.UserService;
import com.jeswin8801.byteBlog.util.AppUtil;
import com.jeswin8801.byteBlog.util.exceptions.ResourceConflictException;
import com.jeswin8801.byteBlog.util.exceptions.ResourceNotFoundException;
import com.jeswin8801.byteBlog.util.exceptions.enums.BlogExceptions;
import com.jeswin8801.byteBlog.util.exceptions.enums.UserExceptions;
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

    @Autowired
    private BlogMapper<BlogsCompactResponseDto> compactResponseDtoBlogMapper;

    @Autowired
    private BlogMapper<BlogDto> blogDtoBlogMapper;

    @Autowired
    private UserMapper<AuthorCompactDto> authorCompactDtoUserMapper;

    @Autowired
    private UserMapper<AuthorDto> authorDtoUserMapper;

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

        String headingUri = postBlogRequestDto.getHeader().replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", "-").toLowerCase();

        if (blogRepository.existsByHeadingUri(headingUri))
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
                        .headingUri(headingUri)
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

    @Override
    public GenericResponseDto<Set<BlogsCompactResponseDto>> getAllBlogsByAuthor(String username) {
        User user = userService.findUserByUsername(username);
        if (Objects.isNull(user))
            throw new ResourceNotFoundException(UserExceptions.USER_RECORD_NOT_FOUND.getMessage());

        List<Blog> blogList = blogRepository.findByUser(user);
        if (CollectionUtils.isEmpty(blogList))
            throw new ResourceNotFoundException(BlogExceptions.NO_BLOGS_FOUND.getMessage());

        return GenericResponseDto.<Set<BlogsCompactResponseDto>>builder()
                .message(
                        new HashSet<>() {{
                            for (Blog blog : blogList) {
                                log.info(AppUtil.toJson(blog));
                                BlogsCompactResponseDto compactResponseDto = compactResponseDtoBlogMapper.toDto(blog, BlogsCompactResponseDto.class);
                                compactResponseDto.setAuthor(authorCompactDtoUserMapper.toDto(user, AuthorCompactDto.class));
                                add(compactResponseDto);
                            }
                        }}
                ).httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponseDto<BlogDto> getBlogByHeading(String headingUri) {
        Blog blog = blogRepository.findByHeadingUri(headingUri);
        if (Objects.isNull(blog))
            throw new ResourceNotFoundException(BlogExceptions.NO_BLOGS_FOUND.getMessage());

        BlogDto blogDto = blogDtoBlogMapper.toDto(blog, BlogDto.class);
        blogDto.setAuthor(authorCompactDtoUserMapper.toDto(blog.getUser(), AuthorCompactDto.class));

        return GenericResponseDto.<BlogDto>builder()
                .message(blogDto)
                .httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponseDto<AuthorDto> getBlogAuthorDetails(String username) {
        User user = userService.findUserByUsername(username);
        if (Objects.isNull(user))
            throw new ResourceNotFoundException(UserExceptions.USER_RECORD_NOT_FOUND.getMessage());

        return GenericResponseDto.<AuthorDto>builder()
                .message(
                        authorDtoUserMapper.toDto(user, AuthorDto.class)
                ).httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public Blog getBlogById(String blogId) {
        return blogRepository.findById(blogId).orElse(null);
    }

    @Override
    public void saveOrUpdateBlog(Blog blog) {
        blogRepository.save(blog);
    }
}
