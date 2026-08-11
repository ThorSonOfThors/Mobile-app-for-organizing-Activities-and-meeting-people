package com.example.springbackend.service;

import com.example.springbackend.dto.LoginRequest;
import com.example.springbackend.dto.RegisterRequest;
import com.example.springbackend.entity.User;
import com.example.springbackend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    

    public UserService(UserRepository userRepository) {
        
        this.userRepository = userRepository;
        
    }


    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User updateUser(Long id, User updatedUser) {

        User user = userRepository
                .findById(id)
                .orElseThrow();

        user.setName(
                updatedUser.getName()
        );

        user.setCountryOfOrigin(
                updatedUser.getCountryOfOrigin()
        );

        user.setProfileImageId(
                updatedUser.getProfileImageId()
        );


        user.setEmail(
                updatedUser.getEmail()
        );

        user.setBio(
                updatedUser.getBio()
        );

        return userRepository.save(
                user
        );
    }



    public User setProfileImage(
        Long userId,
        Long imageId
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        user.setProfileImageId(imageId);

        return userRepository.save(user);
    }

}