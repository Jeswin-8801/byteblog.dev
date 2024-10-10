package com.jeswin8801.byteBlog.entities.converters;

import com.jeswin8801.byteBlog.entities.converters.abstracts.GenericMapper;
import com.jeswin8801.byteBlog.entities.model.Blog;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlogMapper<Dto> implements GenericMapper<Blog, Dto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Blog toEntity(Dto blogDto) {
        return modelMapper.map(blogDto, Blog.class);
    }

    @Override
    public Dto toDto(Blog blog, Class<Dto> className) {
        return modelMapper.map(blog, className);
    }
}
