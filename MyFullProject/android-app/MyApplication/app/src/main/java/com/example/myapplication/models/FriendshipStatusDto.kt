package com.example.myapplication.models

data class FriendshipStatusDto(

    val status: FriendshipStatus,

    val friendshipId: Long?,

    val senderId: Long?

)