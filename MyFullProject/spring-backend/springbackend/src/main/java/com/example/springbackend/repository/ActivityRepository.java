package com.example.springbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springbackend.entity.Activity;

@Repository
public interface ActivityRepository
        extends JpaRepository<Activity, Long> {

    List<Activity> findByCreatorId(Long creatorId);
    Optional<Activity> findByChatId(Long chatId);
}