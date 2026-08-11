package com.example.springbackend.controller;

import com.example.springbackend.dto.LoginRequest;
import com.example.springbackend.dto.LoginResponse;
import com.example.springbackend.dto.LogoutRequest;
import com.example.springbackend.dto.RefreshRequest;
import com.example.springbackend.dto.RefreshResponse;
import com.example.springbackend.dto.RegisterRequest;
import com.example.springbackend.entity.User;
import com.example.springbackend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public RefreshResponse refresh(
            @RequestBody RefreshRequest request
    ) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public void logout(
            @RequestBody LogoutRequest request
    ) {
        authService.logout(request);
    }
}