package com.example.myapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.example.myapplication.components.ParticipantRow
import com.example.myapplication.models.Participant


import com.example.myapplication.models.ActivityDetailsResponse
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    chatId: Long,
    navController: NavController
) {

    // later load from backend



    var activityName by remember {
        mutableStateOf("Loading...")
    }

    var description by remember {
        mutableStateOf("")
    }

    var dateTime by remember {
        mutableStateOf("")
    }

    var participantCount by remember {
        mutableStateOf(0)
    }

    var participants by remember {
        mutableStateOf<List<Participant>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }


    LaunchedEffect(chatId) {

        RetrofitInstance.api
            .getActivityDetails(chatId)
            .enqueue(object : Callback<ActivityDetailsResponse> {

                override fun onResponse(
                    call: Call<ActivityDetailsResponse>,
                    response: Response<ActivityDetailsResponse>
                ) {

                    loading = false

                    if (response.isSuccessful) {

                        response.body()?.let {

                            activityName = it.title
                            description = it.description
                            dateTime = it.eventTime
                            participantCount = it.participantCount
                            participants = it.participants

                        }

                    }

                }

                override fun onFailure(
                    p0: Call<ActivityDetailsResponse?>?,
                    p1: Throwable?
                ) {
                    TODO("Not yet implemented")
                    loading = false
                    p1?.printStackTrace()
                }



            })

    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }



    Scaffold(
    ) { padding ->
        LazyColumn(


            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            item {

                Text(
                    activityName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

            }

            item {

                Text(
                    description,
                    style = MaterialTheme.typography.bodyLarge
                )

            }


            fun formatEventDateTime(dateTime: String): Pair<String, String> {
                return try {
                    val inputFormat = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        Locale.getDefault()
                    )

                    val date = inputFormat.parse(dateTime)

                    val dateFormat = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    )

                    val timeFormat = SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    )

                    Pair(
                        dateFormat.format(date!!),
                        timeFormat.format(date)
                    )

                } catch (e: Exception) {
                    Pair(dateTime, "")
                }
            }



            item {

                val (formattedDate, formattedTime) = formatEventDateTime(dateTime)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Card(
                        modifier = Modifier.weight(1f)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "📅 Date",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.titleMedium
                            )

                        }

                    }

                    Card(
                        modifier = Modifier.weight(1f)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "🕒 Time",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.titleMedium
                            )

                        }

                    }

                }

            }

            item {

                Card {

                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {

                        Text(
                            "Participants ($participantCount)",
                            fontWeight = FontWeight.Bold
                        )

                    }

                }

            }




            items(participants) { participant ->

                ParticipantRow(
                    participant = participant,
                    navController = navController
                )

            }

        }

    }

}