package com.jeswin8801.byteBlog.entities.converters;

import com.jeswin8801.byteBlog.entities.converters.abstracts.GenericMapper;
import com.jeswin8801.byteBlog.entities.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper<Dto> implements GenericMapper<User, Dto> {
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public User toEntity(Dto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    @Override
    public Dto toDto(User user, Class<Dto> className) {
        return modelMapper.map(user, className);
    }
}
