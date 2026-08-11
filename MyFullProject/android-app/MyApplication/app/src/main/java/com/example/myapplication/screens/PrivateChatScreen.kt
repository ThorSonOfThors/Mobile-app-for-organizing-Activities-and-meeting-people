package com.example.myapplication.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// You'll also need these classes from your project:
import com.example.myapplication.network.RetrofitInstance
import com.example.myapplication.models.CreatePrivateChatRequest
import com.example.myapplication.models.ChatResponse


@Composable
fun PrivateChatScreen(
    userId: Long,
    targetUserId: Long,
    navController: NavController
) {

    var input by remember {
        mutableStateOf("")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF0F3)
            )
    ) {


        // HEADER

        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Private Chat",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )

        }



        Spacer(
            modifier = Modifier.weight(1f)
        )



        // INPUT BAR

        Surface(
            tonalElevation = 4.dp
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("Message...")
                    }
                )


                Spacer(
                    Modifier.width(8.dp)
                )


                FloatingActionButton(

                    onClick = {

                        if(input.isBlank())
                            return@FloatingActionButton


                        val request =
                            CreatePrivateChatRequest(
                                user1Id = userId,
                                user2Id = targetUserId,
                                message = input
                            )


                        RetrofitInstance.api
                            .createPrivateChat(request)
                            .enqueue(
                                object :
                                    Callback<ChatResponse> {


                                    override fun onResponse(
                                        call: Call<ChatResponse>,
                                        response: Response<ChatResponse>
                                    ) {


                                        if(response.isSuccessful){

                                            val chatId =
                                                response.body()
                                                    ?.chatId


                                            if(chatId != null){

                                                navController
                                                    .navigate(
                                                        "chat/$chatId"
                                                    )
                                            }
                                        }

                                    }


                                    override fun onFailure(
                                        call: Call<ChatResponse>,
                                        t: Throwable
                                    ) {
                                        t.printStackTrace()
                                    }

                                })


                    }

                ){

                    Icon(
                        Icons.Default.Send,
                        contentDescription = null
                    )

                }

            }

        }

    }

}