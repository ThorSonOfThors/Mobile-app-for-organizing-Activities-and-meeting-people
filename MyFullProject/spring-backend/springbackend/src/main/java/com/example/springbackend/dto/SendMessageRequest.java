package com.example.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class SendMessageRequest {

    private Long senderId;
    private String content;

    private Long replyToMessageId;
    private String replyPreview;
    private String replySenderName;

    // getters setters
}
