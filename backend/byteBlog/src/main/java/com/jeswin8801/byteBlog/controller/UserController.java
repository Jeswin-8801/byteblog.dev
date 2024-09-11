package com.jeswin8801.byteBlog.controller;

import com.jeswin8801.byteBlog.entities.dto.user.ChangePasswordRequestDto;
import com.jeswin8801.byteBlog.service.webapp.user.abstracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto requestDto) {
        return userService.updatePassword(requestDto).getResponseEntity();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String id) {
        return userService.deleteUser(id).getResponseEntity();
    }
}
