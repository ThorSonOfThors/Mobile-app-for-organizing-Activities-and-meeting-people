package com.example.myapplication.models

data class ActivityDetailsResponse(
    val activityId: Long,
    val chatId: Long,
    val title: String,
    val description: String,
    val eventTime: String,
    val participantCount: Int,
    val participants: List<Participant>
)