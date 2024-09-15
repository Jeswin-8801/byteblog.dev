package com.jeswin8801.byteBlog.controller;

import com.jeswin8801.byteBlog.entities.dto.user.ChangePasswordRequestDto;
import com.jeswin8801.byteBlog.entities.dto.user.UserDto;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserDetails(@RequestParam String id) {
        return userService.getUser(id).getResponseEntity();
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUser(@RequestPart("user") UserDto userDto,
                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        return userService.updateUser(userDto, image).getResponseEntity();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String id) {
        return userService.deleteUser(id).getResponseEntity();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto requestDto) {
        return userService.updatePassword(requestDto).getResponseEntity();
    }
}
