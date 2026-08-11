package com.example.myapplication.models

data class SendMessageRequest(

    val senderId: Long,

    val content: String,
    val replyToMessageId: Long?,
    val replyPreview: String?,
    val replySenderName: String?
)