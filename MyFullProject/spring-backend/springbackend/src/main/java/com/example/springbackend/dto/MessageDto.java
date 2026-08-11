package com.example.springbackend.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class MessageDto {

    private Long id;
    private Long senderId;
    private String senderName;
    private Long profileImageId;

    private String content;
    private String status;
    private LocalDateTime sentAt;

    private Long replyToMessageId;
    private String replyPreview;
    private String replySenderName;

    // getters setters
}
