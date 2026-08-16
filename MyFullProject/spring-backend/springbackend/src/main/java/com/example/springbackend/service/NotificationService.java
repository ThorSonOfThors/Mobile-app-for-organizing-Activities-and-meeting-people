package com.example.springbackend.service;

import com.example.springbackend.entity.Notification;
import com.example.springbackend.entity.NotificationType;
import com.example.springbackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Create and save a notification.
     */
    public Notification createNotification(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Long actorUserId,
            Long activityId,
            Long chatId,
            Long messageId
    ) {

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);

        notification.setActorUserId(actorUserId);
        notification.setActivityId(activityId);
        notification.setChatId(chatId);
        notification.setMessageId(messageId);

        notification.setSeen(false);

        return notificationRepository.save(notification);
    }

    /**
     * Get all notifications for a user.
     */
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get only unseen notifications.
     */
    public List<Notification> getUnseenNotifications(Long userId) {
        return notificationRepository
                .findByUserIdAndSeenFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * Get number of unseen notifications.
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository
                .countByUserIdAndSeenFalse(userId);
    }

    /**
     * Mark one notification as seen.
     */
    @Transactional
    public void markAsSeen(Long notificationId, Long userId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found")
                );

        // Important: make sure the notification belongs to this user.
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized notification access");
        }

        notification.setSeen(true);

        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as seen.
     */
    @Transactional
    public void markAllAsSeen(Long userId) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdAndSeenFalseOrderByCreatedAtDesc(userId);

        for (Notification notification : notifications) {
            notification.setSeen(true);
        }

        notificationRepository.saveAll(notifications);
    }
}