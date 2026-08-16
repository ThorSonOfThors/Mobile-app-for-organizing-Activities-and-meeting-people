package com.example.myapplication.models

data class ChatHeaderResponse(

    val activityTitle: String?,

    val participantCount: Int,

    val participants: List<Participant>,

    val isGroup: Boolean,

    val privateChat: Boolean,

    val otherUserId: Long?,

    val otherUserName: String?,

    val otherUserProfileImageId: Long?

)