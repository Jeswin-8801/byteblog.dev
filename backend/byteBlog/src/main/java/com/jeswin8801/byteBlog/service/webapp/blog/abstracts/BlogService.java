package com.jeswin8801.byteBlog.service.webapp.blog.abstracts;

import com.jeswin8801.byteBlog.entities.dto.GenericResponseDto;
import com.jeswin8801.byteBlog.entities.dto.blog.TagsDto;

public interface BlogService {
    GenericResponseDto<TagsDto> getAllTags();
}
