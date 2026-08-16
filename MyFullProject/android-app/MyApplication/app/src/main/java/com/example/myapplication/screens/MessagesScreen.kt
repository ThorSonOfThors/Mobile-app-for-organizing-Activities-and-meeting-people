package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.models.Chat
import com.example.myapplication.network.RetrofitInstance

import com.example.myapplication.components.ProfileImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun MessagesScreen(
    userId: Long?,
    navController: NavController,
    onChatClicked: (Chat) -> Unit = {}
) {

    var chats by remember {
        mutableStateOf<List<Chat>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(userId) {

        println("MessagesScreen userId = $userId")

        if (userId == null) {
            println("UserId is null")
            loading = false
            return@LaunchedEffect
        }

        println("Calling getUserChats...")

        RetrofitInstance.api.getUserChats(userId)
            .enqueue(object : Callback<List<Chat>> {

                override fun onResponse(
                    call: Call<List<Chat>>,
                    response: Response<List<Chat>>
                ) {

                    println("===== CHAT RESPONSE DEBUG =====")
                    println("Response code: ${response.code()}")

                    val receivedChats = response.body()

                    println(
                        "Number of chats received: ${receivedChats?.size}"
                    )

                    receivedChats?.forEach { chat ->

                        println(
                            "chatId=${chat.chatId}, " +
                                    "name=${chat.name}, " +
                                    "isGroup=${chat.isGroup}, " +
                                    "lastMessage=${chat.lastMessage}, " +
                                    "lastMessageAt=${chat.lastMessageAt}, " +
                                    "profilePhoto=${chat.otherUserProfilePhoto}, " +
                                    "unread=${chat.unread}"
                        )
                    }

                    println("===============================")

                    loading = false

                    if (response.isSuccessful) {
                        chats = receivedChats ?: emptyList()
                    }
                }

                override fun onFailure(
                    call: Call<List<Chat>>,
                    t: Throwable
                ) {

                    println("Retrofit error: ${t.message}")
                    t.printStackTrace()

                    loading = false
                }
            })
    }

    when {

        loading -> {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        chats.isEmpty() -> {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("You are not participating in any chats.")
            }
        }

        else -> {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(
                    items = chats,
                    key = { it.chatId }
                ) { chat ->

                    ChatListItem(
                        chat = chat,
                        onClick = {
                            navController.navigate("chat/${chat.chatId}")

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit
) {

    val containerColor =
        if (chat.isGroup) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
             * Avatar
             */
            if (chat.isGroup) {

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.secondary
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Group chat",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }

            } else {

                /*
                 * Temporary private-chat avatar.
                 *
                 * We currently receive the profile image ID
                 * from the backend, not an image URL.
                 *
                 * We'll connect this to your image endpoint next.
                 */
                ProfileImage(
                    imageId = chat.otherUserProfilePhoto?.toLongOrNull(),
                    size = 56.dp,
                    modifier = Modifier.clip(CircleShape)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            /*
             * Chat name + last message
             */
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = chat.name ?: "Chat",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.size(3.dp)
                )

                Text(
                    text = chat.lastMessage
                        ?: "No messages yet",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            /*
             * Unread indicator
             */
            if (chat.unread) {

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.error
                        )
                )
            }
        }
    }
}