package com.example.myapplication.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    public const val BASE_URL =
        "http://192.168.100.64:8081/"

    private val okHttpClient by lazy {

        OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor()
            )
            .build()
    }

    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}