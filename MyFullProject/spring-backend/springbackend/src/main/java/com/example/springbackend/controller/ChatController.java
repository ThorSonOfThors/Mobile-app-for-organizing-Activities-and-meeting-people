package com.example.springbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.springbackend.dto.ChatHeaderResponse;
import com.example.springbackend.dto.MessageDto;
import com.example.springbackend.dto.SendMessageRequest;
import com.example.springbackend.dto.PrivateChatLookupResponse;
import com.example.springbackend.entity.Chat;
import com.example.springbackend.service.ChatService;

import org.springframework.http.ResponseEntity;

import com.example.springbackend.dto.CreatePrivateChatRequest;
import com.example.springbackend.dto.ChatResponse;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/user/{userId}")
    public List<Chat> getUserChats(
            @PathVariable Long userId
    ) {
        return chatService.getUserChats(userId);
    }


    @GetMapping("/{chatId}")
    public ChatHeaderResponse getChatHeader(
            @PathVariable Long chatId
    ) {
        return chatService.getChatHeader(chatId);
    }


    @GetMapping("/{chatId}/messages")
    public List<MessageDto> getMessages(
            @PathVariable Long chatId
    ) {
        return chatService.getMessages(chatId);
    }


    @PostMapping("/{chatId}/messages")
    public MessageDto sendMessage(
            @PathVariable Long chatId,
            @RequestBody SendMessageRequest request
    ) {
        return chatService.sendMessage(chatId, request);
    }


    @PostMapping("/{chatId}/seen/{userId}")
    public void markMessagesAsSeen(
            @PathVariable Long chatId,
            @PathVariable Long userId
    ) {
        chatService.markMessagesAsSeen(chatId, userId);
    }


    @PostMapping("/private")
    public ResponseEntity<ChatResponse> createPrivateChat(
            @RequestBody CreatePrivateChatRequest request
    ) {

        Chat chat =
                chatService.createPrivateChat(
                        request.getUser1Id(),
                        request.getUser2Id(),
                        request.getMessage()
                );


        return ResponseEntity.ok(
                new ChatResponse(
                        chat.getChatId()
                )
        );
    }



    @GetMapping("/private/{otherUserId}")
        public ResponseEntity<PrivateChatLookupResponse> findExistingPrivateChat(
                @PathVariable Long otherUserId
        ) {

        return ResponseEntity.ok(
                chatService.findExistingPrivateChat(otherUserId)
        );
    }
    

}