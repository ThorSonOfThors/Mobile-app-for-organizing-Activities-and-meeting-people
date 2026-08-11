package com.example.springbackend.service;

import com.example.springbackend.entity.UserImage;
import com.example.springbackend.repository.UserImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserImageService {

    private final UserImageRepository repository;

    public UserImageService(
            UserImageRepository repository
    ) {
        this.repository = repository;
    }

    public List<UserImage> getImages(
            Long userId
    ) {
        return repository.findByUserId(userId);
    }

    public UserImage save(
            UserImage image
    ) {
        return repository.save(image);
    }

    public void delete(
            Long imageId
    ) {
        repository.deleteById(imageId);
    }


    public UserImage findById(Long imageId) {

        return repository.findById(imageId)
                .orElseThrow();
    }

    
}
