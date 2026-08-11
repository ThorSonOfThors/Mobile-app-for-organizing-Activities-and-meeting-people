package com.example.myapplication.models

data class Friend(

    val friendshipId: Long,

    val userId: Long,

    val name: String,

    val profileImageId: Long?

)