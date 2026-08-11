package com.example.myapplication.session

import android.content.Context

object SessionManager {

    private const val PREF_NAME = "user_session"

    private const val KEY_USER_ID = "user_id"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    var currentUserId: Long? = null
    private var accessToken: String? = null
    private var refreshToken: String? = null

    fun initialize(context: Context) {

        val prefs = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val id = prefs.getLong(KEY_USER_ID, -1L)

        currentUserId =
            if (id == -1L) null else id

        accessToken =
            prefs.getString(KEY_ACCESS_TOKEN, null)

        refreshToken =
            prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun login(
        context: Context,
        userId: Long,
        accessToken: String,
        refreshToken: String
    ) {

        currentUserId = userId
        this.accessToken = accessToken
        this.refreshToken = refreshToken

        val prefs = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun logout(context: Context) {

        currentUserId = null
        accessToken = null
        refreshToken = null

        val prefs = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        prefs.edit().clear().apply()
    }

    fun getUserId(): Long? {
        return currentUserId
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun getRefreshToken(): String? {
        return refreshToken
    }

    fun setAccessToken(
        context: Context,
        newAccessToken: String
    ) {

        accessToken = newAccessToken

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(KEY_ACCESS_TOKEN, newAccessToken)
            .apply()
    }

    fun isLoggedIn(): Boolean {

        return currentUserId != null &&
                accessToken != null &&
                refreshToken != null
    }

    fun hasAccessToken(): Boolean {
        return !accessToken.isNullOrBlank()
    }

}