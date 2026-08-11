package com.example.springbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"User_Images\"")
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "image_path")
    private String imagePath;

    public UserImage() {}

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}