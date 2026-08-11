package com.example.myapplication.models

data class Activity(
    val activityId: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val creatorName: String?,
    val eventTime: String
)