package com.jeswin8801.byteBlog.entities.converters.abstracts;

public interface GenericMapper<Entity, Dto> {

    Entity toEntity(Dto dto);

    Dto toDto(Entity entity);
}
