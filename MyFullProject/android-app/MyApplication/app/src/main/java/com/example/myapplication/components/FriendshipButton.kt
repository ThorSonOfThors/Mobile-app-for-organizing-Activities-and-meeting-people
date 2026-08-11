package com.example.myapplication.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.models.Friendship
import com.example.myapplication.models.FriendshipStatus
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun FriendshipButton(
    friendshipStatus: FriendshipStatus,
    friendshipId: Long?,
    requestSenderId: Long?,
    currentUserId: Long,
    viewedUserId: Long,
    onStatusChanged: (FriendshipStatus) -> Unit
) {

    Button(

        modifier = Modifier.fillMaxWidth(),

        enabled = friendshipStatus != FriendshipStatus.PENDING ||
                requestSenderId != currentUserId,

        onClick = {

            when (friendshipStatus) {

                FriendshipStatus.NONE -> {

                    RetrofitInstance.api.sendFriendRequest(
                        currentUserId,
                        viewedUserId
                    ).enqueue(object : Callback<Friendship> {

                        override fun onResponse(
                            call: Call<Friendship>,
                            response: Response<Friendship>
                        ) {

                            if (response.isSuccessful) {
                                onStatusChanged(FriendshipStatus.PENDING)
                            }

                        }

                        override fun onFailure(
                            call: Call<Friendship>,
                            t: Throwable
                        ) {
                        }

                    })

                }

                FriendshipStatus.ACCEPTED -> {

                    friendshipId?.let {

                        RetrofitInstance.api.removeFriend(
                            it,
                            currentUserId
                        ).enqueue(object : Callback<Void> {

                            override fun onResponse(
                                call: Call<Void>,
                                response: Response<Void>
                            ) {

                                if (response.isSuccessful) {
                                    onStatusChanged(FriendshipStatus.NONE)
                                }

                            }

                            override fun onFailure(
                                call: Call<Void>,
                                t: Throwable
                            ) {
                            }

                        })

                    }

                }

                FriendshipStatus.PENDING -> {

                    if (requestSenderId != currentUserId) {

                        friendshipId?.let {

                            RetrofitInstance.api.acceptFriendRequest(
                                it,
                                currentUserId!!
                            ).enqueue(object : Callback<Friendship> {

                                override fun onResponse(
                                    call: Call<Friendship>,
                                    response: Response<Friendship>
                                ) {

                                    if (response.isSuccessful) {
                                        onStatusChanged(FriendshipStatus.ACCEPTED)
                                    }

                                }

                                override fun onFailure(
                                    call: Call<Friendship>,
                                    t: Throwable
                                ) {
                                }

                            })

                        }

                    }

                }

                FriendshipStatus.DECLINED -> {

                    RetrofitInstance.api.sendFriendRequest(
                        currentUserId!!,
                        viewedUserId
                    ).enqueue(object : Callback<Friendship> {

                        override fun onResponse(
                            call: Call<Friendship>,
                            response: Response<Friendship>
                        ) {

                            if (response.isSuccessful) {
                                onStatusChanged(FriendshipStatus.PENDING)
                            }

                        }

                        override fun onFailure(
                            call: Call<Friendship>,
                            t: Throwable
                        ) {
                        }

                    })

                }
            }

        }

    ) {

        Text(

            when (friendshipStatus) {

                FriendshipStatus.NONE ->
                    "Add Friend"

                FriendshipStatus.PENDING ->
                    if (requestSenderId == currentUserId)
                        "Request Sent"
                    else
                        "Accept Friend Request"

                FriendshipStatus.ACCEPTED ->
                    "Remove Friend"

                FriendshipStatus.DECLINED ->
                    "Add Friend"
            }

        )

    }

}