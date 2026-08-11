package com.example.springbackend.dto;

public class PrivateChatLookupResponse {

    private boolean exists;
    private Long chatId;

    public PrivateChatLookupResponse() {
    }

    public PrivateChatLookupResponse(boolean exists, Long chatId) {
        this.exists = exists;
        this.chatId = chatId;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}