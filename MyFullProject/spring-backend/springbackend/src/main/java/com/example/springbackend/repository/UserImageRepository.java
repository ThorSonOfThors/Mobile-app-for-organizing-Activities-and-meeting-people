package com.example.springbackend.repository;

import com.example.springbackend.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserImageRepository
        extends JpaRepository<UserImage, Long> {

    List<UserImage> findByUserId(Long userId);
}