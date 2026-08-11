package com.example.myapplication.models

data class CreateActivityRequest(
    val creatorId: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val eventTime: String
)




