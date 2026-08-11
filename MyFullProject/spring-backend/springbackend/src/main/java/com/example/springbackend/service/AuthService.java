package com.example.springbackend.service;

import com.example.springbackend.dto.LoginRequest;
import com.example.springbackend.dto.LoginResponse;
import com.example.springbackend.dto.LogoutRequest;
import com.example.springbackend.dto.RefreshRequest;
import com.example.springbackend.dto.RefreshResponse;
import com.example.springbackend.dto.RegisterRequest;
import com.example.springbackend.entity.RefreshToken;
import com.example.springbackend.entity.User;
import com.example.springbackend.repository.RefreshTokenRepository;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.security.CustomUserDetails;
import com.example.springbackend.security.JwtService;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }


    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }



    public User register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setCountryOfOrigin(request.getCountryOfOrigin());
        user.setCreatedAt(LocalDateTime.now());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        return userRepository.save(user);
    }


    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                generateRefreshToken();

        RefreshToken token = new RefreshToken();

        token.setToken(refreshToken);
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(30));
        token.setRevoked(false);

        refreshTokenRepository.save(token);

        return new LoginResponse(
                accessToken,
                refreshToken,
                user
        );
    }

    public RefreshResponse refresh(RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked.");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired.");
        }

        User user = refreshToken.getUser();

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        String newAccessToken =
                jwtService.generateAccessToken(userDetails);

        return new RefreshResponse(newAccessToken);
    }

    public void logout(LogoutRequest request) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found."));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }



}