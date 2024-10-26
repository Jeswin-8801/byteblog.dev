package com.jeswin8801.byteBlog.entities.converters;

import com.jeswin8801.byteBlog.entities.dto.blog.AuthorCompactDto;
import com.jeswin8801.byteBlog.entities.dto.comment.CommentDto;
import com.jeswin8801.byteBlog.entities.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
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

    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        commentDto.setAuthor(userMapper.toDto(comment.getUser(), AuthorCompactDto.class));
        if (Objects.nonNull(comment.getChildReplyComments())) {
            Set<CommentDto> childrenDto = comment.getChildReplyComments().stream()
                    .map(this::toDto)
                    .collect(Collectors.toSet());
            commentDto.setChildReplyComments(childrenDto);
        }
        return commentDto;
    }
}
