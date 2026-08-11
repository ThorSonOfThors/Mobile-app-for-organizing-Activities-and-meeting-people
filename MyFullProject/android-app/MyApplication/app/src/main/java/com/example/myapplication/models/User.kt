package com.example.myapplication.models

data class User(
    val id: Long?,
    var name: String,
    var countryOfOrigin: String,
    var email: String,
    val bio: String?,
    val profileImageId: Long?,
    val createdAt: String? = null
)