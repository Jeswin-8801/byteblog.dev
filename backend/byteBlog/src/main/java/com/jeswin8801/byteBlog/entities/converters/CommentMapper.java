package com.jeswin8801.byteBlog.entities.converters;

import com.jeswin8801.byteBlog.entities.dto.blog.AuthorCompactDto;
import com.jeswin8801.byteBlog.entities.dto.comment.CommentCompactDto;
import com.jeswin8801.byteBlog.entities.dto.comment.CommentDto;
import com.jeswin8801.byteBlog.entities.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserMapper<AuthorCompactDto> userMapper;

    public Comment toEntity(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        commentDto.setAuthor(userMapper.toDto(comment.getUser(), AuthorCompactDto.class));
        if (Objects.nonNull(comment.getChildReplyComments())) {
            Set<CommentDto> childrenDto = comment.getChildReplyComments().stream()
                    .map(this::toCommentDto)
                    .collect(Collectors.toSet());
            commentDto.setChildReplyComments(childrenDto);
        }
        return commentDto;
    }

    public CommentCompactDto toCommentCompactDto(Comment comment) {
        CommentCompactDto commentCompactDto = modelMapper.map(comment, CommentCompactDto.class);
        commentCompactDto.setBlogHeadingUri(comment.getBlog().getHeadingUri());
        if (Objects.nonNull(comment.getChildReplyComments()))
            commentCompactDto.setReplyCommentAuthorProfileImageUris(
                    new HashSet<>() {{
                        List<Comment> replyComments = new ArrayList<>(comment.getChildReplyComments());
                        if (comment.getChildReplyComments().size() > 3)
                            replyComments.sort(Comparator.comparing(Comment::getLastUpdated));
                        replyComments.subList(0, Math.min(3, replyComments.size())).forEach(comment -> add(comment.getUser().getProfileImageUrl()));
                    }}
            );
        return commentCompactDto;
    }
}
