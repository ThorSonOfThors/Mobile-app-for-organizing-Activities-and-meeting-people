package com.example.myapplication.models

data class CreatePrivateChatRequest(

    val user1Id: Long,
    val user2Id: Long,
    val message: String

)