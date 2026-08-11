package com.example.myapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.models.Chat
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MessagesScreen(
    userId: Long?,
    navController: NavController,
    onChatClicked: (Chat) -> Unit = {}
) {

    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

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

                    println("Number of chats received: ${receivedChats?.size}")

                    receivedChats?.forEach { chat ->
                        println(
                            "chatId=${chat.chatId}, " +
                                    "name=${chat.name}, " +
                                    "isGroup=${chat.isGroup}, " +
                                    "lastMessageAt=${chat.lastMessageAt}"
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

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("chat/${chat.chatId}")
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = chat.name ?: "Private Chat",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = if (chat.isGroup)
                                    "Group Chat"
                                else
                                    "Private Chat",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}