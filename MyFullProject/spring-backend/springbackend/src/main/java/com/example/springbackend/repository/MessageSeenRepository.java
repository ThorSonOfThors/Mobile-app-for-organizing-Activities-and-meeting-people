package com.example.springbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springbackend.entity.MessageSeen;

public interface MessageSeenRepository extends JpaRepository<MessageSeen, Long> {

    boolean existsByMessageIdAndUserId(
            Long messageId,
            Long userId
    );

    long countByMessageId(Long messageId);

}