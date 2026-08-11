package com.example.springbackend.controller;

import com.example.springbackend.entity.User;
import com.example.springbackend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {
        return userService.updateUser(id, user);
    }

    @PutMapping("/{userId}/profile-image/{imageId}")
    public User setProfileImage(
            @PathVariable Long userId,
            @PathVariable Long imageId
    ) {
        return userService.setProfileImage(
                userId,
                imageId
        );
    }
}