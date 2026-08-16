package com.example.springbackend.controller;

import com.example.springbackend.entity.Notification;
import com.example.springbackend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    /**
     * Get all notifications for a user.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                notificationService.getNotifications(userId)
        );
    }

    /**
     * Get unseen notifications.
     */
    @GetMapping("/{userId}/unseen")
    public ResponseEntity<List<Notification>> getUnseenNotifications(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                notificationService.getUnseenNotifications(userId)
        );
    }

    /**
     * Get number of unseen notifications.
     */
    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                notificationService.getUnreadCount(userId)
        );
    }

    /**
     * Mark a notification as seen.
     */
    @PatchMapping("/{notificationId}/seen")
    public ResponseEntity<Void> markAsSeen(
            @PathVariable Long notificationId,
            @RequestParam Long userId
    ) {

        notificationService.markAsSeen(
                notificationId,
                userId
        );

        return ResponseEntity.ok().build();
    }

    /**
     * Mark all notifications as seen.
     */
    @PatchMapping("/{userId}/seen-all")
    public ResponseEntity<Void> markAllAsSeen(
            @PathVariable Long userId
    ) {

        notificationService.markAllAsSeen(userId);

        return ResponseEntity.ok().build();
    }
}