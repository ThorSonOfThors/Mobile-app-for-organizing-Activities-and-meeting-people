package com.example.myapplication.models

data class Chat(
    val chatId: Long,
    val name: String?,
    val isGroup: Boolean,
    val lastMessageAt: String?,
    val lastMessage: String?,
    val otherUserProfilePhoto: String?,
    val unread: Boolean
)