package com.example.myapplication.models

data class Notification(
    val notificationId: Long,
    val userId: Long,
    val type: String,
    val title: String,
    val message: String?,
    val actorUserId: Long?,
    val activityId: Long?,
    val chatId: Long?,
    val messageId: Long?,
    val seen: Boolean,
    val createdAt: String
)