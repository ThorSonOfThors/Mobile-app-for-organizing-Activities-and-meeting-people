package com.example.springbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbackend.entity.Chat;

@Repository
public interface ChatRepository
        extends JpaRepository<Chat, Long> {


    @Query("""
        SELECT c
        FROM Chat c
        JOIN ChatUser cu
            ON c.chatId = cu.chatId
        WHERE cu.userId = :userId
        ORDER BY c.lastMessageAt DESC
    """)
    List<Chat> findChatsByUserId(
            @Param("userId") Long userId
    );



    @Query("""
        SELECT c
        FROM Chat c
        WHERE c.isGroup = false
        AND c.chatId IN (
            SELECT cu1.chatId
            FROM ChatUser cu1
            WHERE cu1.userId = :user1Id
        )
        AND c.chatId IN (
            SELECT cu2.chatId
            FROM ChatUser cu2
            WHERE cu2.userId = :user2Id
        )
    """)
    List<Chat> findPrivateChatBetweenUsers(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );

}