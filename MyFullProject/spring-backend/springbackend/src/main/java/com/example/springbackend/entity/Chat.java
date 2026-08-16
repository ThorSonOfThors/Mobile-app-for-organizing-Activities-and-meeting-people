package com.example.springbackend.entity;

import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "\"Chats\"")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    private String name;

    @Column(name = "is_group", nullable = false)
    private Boolean isGroup;

    private LocalDateTime lastMessageAt = LocalDateTime.now();

    @Transient
    private String lastMessage;

    @Transient
    private Boolean unread;

    @Transient
    private String otherUserProfilePhoto;
}

