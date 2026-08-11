package com.example.myapplication.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.models.FriendRequest
import com.example.myapplication.models.Friendship
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun FriendRequestRow(
    request: FriendRequest,
    currentUserId: Long,
    navController: NavController,
    onUpdated: () -> Unit
) {

    val imageUrl = request.profileImageId?.let {
        "${RetrofitInstance.BASE_URL}api/users/profile-image/$it"
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController.navigate("userProfile/${request.senderId}")
                    },

                verticalAlignment = Alignment.CenterVertically
            ) {

                if (imageUrl != null) {

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,

                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),

                        contentScale = ContentScale.Crop
                    )

                } else {

                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Text(request.name.first().toString())

                        }

                    }

                }

                Spacer(Modifier.width(16.dp))

                Text(
                    request.name,
                    style = MaterialTheme.typography.bodyLarge
                )

            }

            Button(

                onClick = {

                    RetrofitInstance.api.acceptFriendRequest(
                        request.friendshipId,
                        currentUserId
                    ).enqueue(object : Callback<Friendship> {

                        override fun onResponse(
                            call: Call<Friendship>,
                            response: Response<Friendship>
                        ) {

                            if (response.isSuccessful) {
                                onUpdated()
                            }

                        }

                        override fun onFailure(
                            call: Call<Friendship>,
                            t: Throwable
                        ) {
                        }

                    })

                }

            ) {

                Text("✓")

            }

            Spacer(Modifier.width(8.dp))

            Button(

                onClick = {

                    RetrofitInstance.api.declineFriendRequest(
                        request.friendshipId,
                        currentUserId
                    ).enqueue(object : Callback<Friendship> {

                        override fun onResponse(
                            call: Call<Friendship>,
                            response: Response<Friendship>
                        ) {

                            if (response.isSuccessful) {
                                onUpdated()
                            }

                        }

                        override fun onFailure(
                            call: Call<Friendship>,
                            t: Throwable
                        ) {
                        }

                    })

                }

            ) {

                Text("✕")

            }

        }

    }

}