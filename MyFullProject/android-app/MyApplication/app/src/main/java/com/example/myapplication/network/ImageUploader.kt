package com.example.myapplication.network

import android.content.Context
import android.net.Uri
import com.example.myapplication.models.UserImage
import com.example.myapplication.session.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun uploadImage(
    context: Context,
    uri: Uri
) {

    val inputStream =
        context.contentResolver
            .openInputStream(uri)

    val bytes =
        inputStream!!.readBytes()

    val requestFile =
        bytes.toRequestBody(
            "image/*"
                .toMediaTypeOrNull()
        )

    val part =
        MultipartBody.Part
            .createFormData(
                "file",
                "image.jpg",
                requestFile
            )

    val userId =
        SessionManager.currentUserId
            ?: return

    RetrofitInstance.api
        .uploadImage(
            userId,
            part
        )
        .enqueue(
            object :
                retrofit2.Callback<UserImage> {

                override fun onResponse(
                    call: retrofit2.Call<UserImage>,
                    response: retrofit2.Response<UserImage>
                ) {

                    android.util.Log.d(
                        "UPLOAD",
                        "Success"
                    )
                }

                override fun onFailure(
                    call: retrofit2.Call<UserImage>,
                    t: Throwable
                ) {

                    android.util.Log.e(
                        "UPLOAD",
                        t.message ?: "error"
                    )
                }
            }
        )
}