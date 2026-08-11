package com.example.springbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springbackend.entity.ChatUser;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

        boolean existsByChatIdAndUserId(Long chatId, Long userId);
        List<ChatUser> findByChatId(Long chatId);
}
