package com.jeswin8801.byteBlog.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MessageResponseDto {
    private String message;
}
