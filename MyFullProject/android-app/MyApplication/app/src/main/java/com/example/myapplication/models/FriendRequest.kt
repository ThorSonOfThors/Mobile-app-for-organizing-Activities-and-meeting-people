package com.example.myapplication.models


data class FriendRequest(

    val friendshipId: Long,

    val senderId: Long,

    val name: String,

    val profileImageId: Long?

)