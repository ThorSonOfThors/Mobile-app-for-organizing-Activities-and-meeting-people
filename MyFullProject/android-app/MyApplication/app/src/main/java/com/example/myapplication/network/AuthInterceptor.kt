package com.example.myapplication.network

import com.example.myapplication.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val originalRequest = chain.request()

        val accessToken =
            SessionManager.getAccessToken()

        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest =
            originalRequest.newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $accessToken"
                )
                .build()

        println("TOKEN = $accessToken")
        return chain.proceed(authenticatedRequest)
    }
}