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
import com.example.myapplication.models.Friend
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun FriendRow(
    friend: Friend,
    currentUserId: Long,
    navController: NavController,
    onRemoved: () -> Unit
) {

    val imageUrl = friend.profileImageId?.let {
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
                        navController.navigate("userProfile/${friend.userId}")
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

                            Text(friend.name.first().toString())

                        }

                    }

                }

                Spacer(Modifier.width(16.dp))

                Text(
                    friend.name,
                    style = MaterialTheme.typography.bodyLarge
                )

            }

            Button(

                onClick = {

                    RetrofitInstance.api.removeFriend(
                        friend.friendshipId,
                        currentUserId
                    ).enqueue(object : Callback<Void> {

                        override fun onResponse(
                            call: Call<Void>,
                            response: Response<Void>
                        ) {

                            if (response.isSuccessful) {
                                onRemoved()
                            }

                        }

                        override fun onFailure(
                            call: Call<Void>,
                            t: Throwable
                        ) {
                        }

                    })

                }

            ) {

                Text("Unfriend")

            }

        }

    }

}