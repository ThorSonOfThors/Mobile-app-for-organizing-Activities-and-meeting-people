package com.example.myapplication.network

import com.example.myapplication.models.Activity
import com.example.myapplication.models.ActivityDetailsResponse
import com.example.myapplication.models.Chat
import com.example.myapplication.models.ChatHeaderResponse
import com.example.myapplication.models.ChatResponse
import com.example.myapplication.models.LoginRequest
import com.example.myapplication.models.RegisterRequest
import com.example.myapplication.models.CreateActivityRequest
import com.example.myapplication.models.CreatePrivateChatRequest
import com.example.myapplication.models.Friend
import com.example.myapplication.models.FriendRequest
import com.example.myapplication.models.Friendship
import com.example.myapplication.models.FriendshipStatusDto
import com.example.myapplication.models.LoginResponse
import com.example.myapplication.models.LogoutRequest
import com.example.myapplication.models.MessageDto
import com.example.myapplication.models.Participant
import com.example.myapplication.models.PrivateChatLookupResponse
import com.example.myapplication.models.RefreshRequest
import com.example.myapplication.models.RefreshResponse
import com.example.myapplication.models.SendMessageRequest
import com.example.myapplication.models.User
import com.example.myapplication.models.UserImage
import okhttp3.MultipartBody
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<User>

    @POST("/api/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("/api/auth/refresh")
    fun refresh(
        @Body request: RefreshRequest
    ): Call<RefreshResponse>

    @POST("/api/auth/logout")
    fun logout(
        @Body request: LogoutRequest
    ): Call<Void>

    @GET("api/users/{id}")
    fun getUser(
        @Path("id") id: Long
    ): Call<User>

    @PUT("api/users/{id}")
    fun updateUser(
        @Path("id") id: Long,
        @Body user: User
    ): Call<User>


    @Multipart
    @POST("api/users/{id}/images")
    fun uploadImage(
        @Path("id") userId: Long,

        @Part file: MultipartBody.Part
    ): Call<UserImage>


    @GET("api/users/{id}/images")
    fun getImages(
        @Path("id") userId: Long
    ): Call<List<UserImage>>

    @DELETE("api/users/{userId}/images/{imageId}")
    fun deleteImage(
        @Path("userId") userId: Long,
        @Path("imageId") imageId: Long
    ): Call<Void>


    @GET("api/activities")
    fun getActivities(): Call<List<Activity>>

    @POST("api/activities")
    fun createActivity(
        @Body request: CreateActivityRequest
    ): Call<Activity>


    @POST("api/activities/{activityId}/join/{userId}")
    fun joinActivity(
        @Path("activityId") activityId: Long,
        @Path("userId") userId: Long
    ): Call<Activity>


    @GET("api/chats/user/{userId}")
    fun getUserChats(
        @Path("userId") userId: Long
    ): Call<List<Chat>>


    @GET("api/chats/{chatId}")
    fun getChatHeader(
        @Path("chatId") chatId: Long
    ): Call<ChatHeaderResponse>


    @GET("api/chats/{chatId}/messages")
    fun getMessages(
        @Path("chatId") chatId: Long
    ): Call<List<MessageDto>>

    @POST("api/chats/{chatId}/messages")
    fun sendMessage(
        @Path("chatId") chatId: Long,
        @Body request: SendMessageRequest
    ): Call<MessageDto>

    @POST("api/chats/{chatId}/seen/{userId}")
    fun markMessagesSeen(
        @Path("chatId") chatId: Long,
        @Path("userId") userId: Long
    ): Call<Void>


    @GET("api/users/profile-image/{imageId}")
    fun getProfileImage(
        @Path("imageId") imageId: Long
    ): Call<ResponseBody>

    @GET("api/activities/chat/{chatId}/details")
    fun getActivityDetails(
        @Path("chatId") chatId: Long
    ): Call<ActivityDetailsResponse>


    @GET("api/friends/status")
    fun getFriendshipStatus(

        @Query("user1")
        user1: Long?,

        @Query("user2")
        user2: Long

    ): Call<FriendshipStatusDto>


    @POST("api/friends/request")
    fun sendFriendRequest(

        @Query("senderId")
        senderId: Long?,

        @Query("receiverId")
        receiverId: Long

    ): Call<Friendship>


    @POST("api/friends/{friendshipId}/accept")
    fun acceptFriendRequest(

        @Path("friendshipId")
        friendshipId: Long,

        @Query("receiverId")
        receiverId: Long

    ): Call<Friendship>


    @POST("api/friends/{friendshipId}/decline")
    fun declineFriendRequest(

        @Path("friendshipId")
        friendshipId: Long,

        @Query("receiverId")
        receiverId: Long

    ): Call<Friendship>


    @DELETE("api/friends/{friendshipId}")
    fun removeFriend(

        @Path("friendshipId")
        friendshipId: Long,

        @Query("userId")
        userId: Long?

    ): Call<Void>


    @GET("api/friends/{userId}")
    fun getFriends(

        @Path("userId")
        userId: Long

    ): Call<List<Friend>>


    @GET("api/friends/pending/{userId}")
    fun getPendingRequests(

        @Path("userId")
        userId: Long

    ): Call<List<FriendRequest>>



    @GET("api/friends/mutual")
    fun getMutualFriends(

        @Query("user1")
        user1: Long,

        @Query("user2")
        user2: Long

    ): Call<List<Friend>>

    @POST("api/chats/private")
    fun createPrivateChat(
        @Body request: CreatePrivateChatRequest
    ): Call<ChatResponse>

    @GET("api/chats/private/{otherUserId}")
    fun findExistingPrivateChat(
        @Path("otherUserId") otherUserId: Long
    ): Call<PrivateChatLookupResponse>


}