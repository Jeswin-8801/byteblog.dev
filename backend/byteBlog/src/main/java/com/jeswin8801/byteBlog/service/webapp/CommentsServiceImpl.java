package com.jeswin8801.byteBlog.service.webapp;

import com.jeswin8801.byteBlog.entities.converters.CommentMapper;
import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.comment.CommentCompactDto;
import com.jeswin8801.byteBlog.entities.dto.comment.CommentDto;
import com.jeswin8801.byteBlog.entities.dto.comment.PostCommentDto;
import com.jeswin8801.byteBlog.entities.model.Blog;
import com.jeswin8801.byteBlog.entities.model.Comment;
import com.jeswin8801.byteBlog.entities.model.User;
import com.jeswin8801.byteBlog.repository.CommentsRepository;
import com.jeswin8801.byteBlog.service.webapp.abstracts.BlogService;
import com.jeswin8801.byteBlog.service.webapp.abstracts.CommentsService;
import com.jeswin8801.byteBlog.service.webapp.abstracts.UserService;
import com.jeswin8801.byteBlog.util.exceptions.ResourceNotFoundException;
import com.jeswin8801.byteBlog.util.exceptions.enums.BlogExceptions;
import com.jeswin8801.byteBlog.util.exceptions.enums.UserExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public GenericResponseDto<Set<CommentDto>> getCommentsForBlog(String blogId) {
        Blog blog = blogService.getBlogById(blogId);
        if (Objects.isNull(blog))
            throw new ResourceNotFoundException(BlogExceptions.INVALID_BLOG_ID.getMessage());

        if (CollectionUtils.isEmpty(blog.getComments()))
            throw new ResourceNotFoundException(BlogExceptions.NO_COMMENTS_FOUND.getMessage());

        return GenericResponseDto.<Set<CommentDto>>builder()
                .message(
                        blog.getComments()
                                .stream()
                                .filter(comment -> Objects.isNull(comment.getParentComment())) // filter out nested comments
                                .map(comment -> commentMapper.toCommentDto(comment))
                                .collect(Collectors.toSet())
                ).httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponseDto<MessageResponseDto> postComment(PostCommentDto postCommentDto) {
        User user = userService.findUserById(postCommentDto.getUserId());
        if (Objects.isNull(user))
            throw new ResourceNotFoundException(UserExceptions.INVALID_USER_ID.getMessage());

        Blog blog = blogService.getBlogById(postCommentDto.getBlogId());
        if (Objects.isNull(blog))
            throw new ResourceNotFoundException(BlogExceptions.INVALID_BLOG_ID.getMessage());

        Comment parentComment = null;
        if (postCommentDto.getParentCommentId() != null && postCommentDto.getParentCommentId() > 0L) {
            parentComment = commentsRepository.findById(postCommentDto.getParentCommentId()).orElse(null);
            if (Objects.isNull(parentComment))
                throw new ResourceNotFoundException(BlogExceptions.INVALID_COMMENT_ID.getMessage());
        }

        Comment comment = Comment.builder()
                .comment(postCommentDto.getComment())
                .user(user)
                .blog(blog)
                .parentComment(parentComment)
                .build();

        commentsRepository.save(comment);

        if (Objects.nonNull(parentComment)) {
            parentComment.getChildReplyComments().add(comment);
            commentsRepository.save(parentComment);
        }

        return GenericResponseDto.<MessageResponseDto>builder()
                .message(
                        MessageResponseDto.builder()
                                .message("Successfully added comment")
                                .build()
                ).httpStatusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponseDto<Set<CommentCompactDto>> getCommentsByUser(String username) {
        User user = userService.findUserByUsername(username);
        if (Objects.isNull(user))
            throw new ResourceNotFoundException(UserExceptions.USER_RECORD_NOT_FOUND.getMessage());

        if (CollectionUtils.isEmpty(user.getComments()))
            throw new ResourceNotFoundException(BlogExceptions.NO_COMMENTS_FOUND.getMessage());

        return GenericResponseDto.<Set<CommentCompactDto>>builder()
                .message(
                        user.getComments()
                                .stream()
                                .map(comment -> commentMapper.toCommentCompactDto(comment))
                                .collect(Collectors.toSet())
                ).httpStatusCode(HttpStatus.OK)
                .build();
    }
}
