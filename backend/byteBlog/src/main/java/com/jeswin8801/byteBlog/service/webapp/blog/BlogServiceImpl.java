package com.jeswin8801.byteBlog.service.webapp.blog;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.blog.TagsDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.entities.model.enums.BlogTags;
import com.jeswin8801.byteBlog.service.webapp.blog.abstracts.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogServiceImpl implements BlogService {
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
}
