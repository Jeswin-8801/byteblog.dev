package com.jeswin8801.byteBlog.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponseDto<T> {

    private T message;

    private HttpStatusCode httpStatusCode;

    public ResponseEntity<?> getResponseEntity() {
        return new ResponseEntity<>(message, httpStatusCode);
    }
}
