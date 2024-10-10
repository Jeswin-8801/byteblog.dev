package com.jeswin8801.byteBlog.entities.converters;

import com.jeswin8801.byteBlog.entities.dto.blog.CommentDto;
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

    public Comment toEntity(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }

    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        if (Objects.nonNull(comment.getChildReplyComments())) {
            Set<CommentDto> childrenDto = comment.getChildReplyComments().stream()
                    .map(this::toDto)
                    .collect(Collectors.toSet());
            commentDto.setChildReplyComments(childrenDto);
        }
        return commentDto;
    }
}
