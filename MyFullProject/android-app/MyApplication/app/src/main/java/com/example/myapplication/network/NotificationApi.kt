package com.example.myapplication.network

import com.example.myapplication.models.Notification
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {

    @GET("api/notifications/{userId}")
    suspend fun getNotifications(
        @Path("userId") userId: Long
    ): List<Notification>

    @GET("api/notifications/{userId}/unseen")
    suspend fun getUnseenNotifications(
        @Path("userId") userId: Long
    ): List<Notification>

    @GET("api/notifications/{userId}/unread-count")
    suspend fun getUnreadCount(
        @Path("userId") userId: Long
    ): Long

    @PATCH("api/notifications/{notificationId}/seen")
    suspend fun markAsSeen(
        @Path("notificationId") notificationId: Long,
        @Query("userId") userId: Long
    )

    @PATCH("api/notifications/{userId}/seen-all")
    suspend fun markAllAsSeen(
        @Path("userId") userId: Long
    )
}