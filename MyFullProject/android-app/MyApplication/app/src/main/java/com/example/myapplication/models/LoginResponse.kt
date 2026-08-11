package com.example.myapplication.models

data class LoginResponse(

    val accessToken: String,

    val refreshToken: String,

    val user: User

)