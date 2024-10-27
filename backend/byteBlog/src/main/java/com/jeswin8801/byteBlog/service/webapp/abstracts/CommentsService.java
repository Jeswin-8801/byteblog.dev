package com.jeswin8801.byteBlog.service.webapp.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.MessageResponseDto;
import com.jeswin8801.byteBlog.entities.dto.comment.CommentCompactDto;
import com.jeswin8801.byteBlog.entities.dto.comment.CommentDto;
import com.jeswin8801.byteBlog.entities.dto.comment.PostCommentDto;

import java.util.Set;

public interface CommentsService {

    GenericResponseDto<Set<CommentDto>> getCommentsForBlog(String blogId);

    GenericResponseDto<MessageResponseDto> postComment(PostCommentDto postCommentDto);

    GenericResponseDto<Set<CommentCompactDto>> getCommentsByUser(String username);
}
