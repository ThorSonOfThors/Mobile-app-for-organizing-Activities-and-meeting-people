package com.example.myapplication.models

data class Friendship(

    val friendshipId: Long,

    val senderId: Long,

    val receiverId: Long,

    val status: String,

    val createdAt: String?

)