package com.example.myapplication.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.models.Friend
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MutualFriendsSection(
    currentUserId: Long,
    viewedUserId: Long,
    navController: NavController
) {

    var friends by remember {
        mutableStateOf<List<Friend>>(emptyList())
    }

    LaunchedEffect(viewedUserId) {

        RetrofitInstance.api.getMutualFriends(
            currentUserId,
            viewedUserId
        ).enqueue(object : Callback<List<Friend>> {

            override fun onResponse(
                call: Call<List<Friend>>,
                response: Response<List<Friend>>
            ) {

                if (response.isSuccessful) {
                    friends = response.body() ?: emptyList()
                }

            }

            override fun onFailure(
                call: Call<List<Friend>>,
                t: Throwable
            ) {
            }

        })

    }

    if (friends.isEmpty()) return

    Column {

        Text(
            text = "Mutual Friends (${friends.size})",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(friends.take(8)) { friend ->

                ProfileImage(
                    imageId = friend.profileImageId,
                    size = 52.dp,
                    modifier = Modifier.clickable {

                        navController.navigate(
                            "userProfile/${friend.userId}"
                        )

                    }
                )

            }

            if (friends.size > 8) {

                item {

                    Box(
                        modifier = Modifier
                            .size(52.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {

                        Text("+${friends.size - 8}")

                    }

                }

            }

        }

    }

}