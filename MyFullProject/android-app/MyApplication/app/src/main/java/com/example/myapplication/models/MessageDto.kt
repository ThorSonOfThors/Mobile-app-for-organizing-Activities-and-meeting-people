package com.example.myapplication.models

data class MessageDto(

    val id: Long,

    val senderId: Long,

    val senderName: String,

    val senderProfileImageId: Long?,

    val content: String,

    val status: String,

    val sentAt: String,

    val replyToMessageId: Long?,

    val replyPreview: String?,

    val replySenderName: String?
)