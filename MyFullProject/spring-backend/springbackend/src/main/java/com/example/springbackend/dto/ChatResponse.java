package com.example.springbackend.dto;

public class ChatResponse {

    private Long chatId;


    public ChatResponse(Long chatId) {
        this.chatId = chatId;
    }


    public Long getChatId() {
        return chatId;
    }


    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}