package com.example.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.models.User
import com.example.myapplication.models.UserImage
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.unit.times
import com.example.myapplication.components.FriendshipButton
import com.example.myapplication.components.MutualFriendsSection
import com.example.myapplication.models.FriendshipStatus
import com.example.myapplication.models.FriendshipStatusDto
import com.example.myapplication.models.PrivateChatLookupResponse
import com.example.myapplication.session.SessionManager

@Composable
fun ProfileViewScreen(
    userId: Long,
    currentUserId: Long? = SessionManager.currentUserId,
    navController: NavController
) {

    var loading by remember {
        mutableStateOf(true)
    }

    var user by remember {
        mutableStateOf<User?>(null)
    }

    var images by remember {
        mutableStateOf<List<UserImage>>(emptyList())
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var friendshipStatus: FriendshipStatus by remember {
        mutableStateOf(FriendshipStatus.NONE)
    }

    var friendshipId by remember {
        mutableStateOf<Long?>(null)
    }

    var requestSenderId by remember {
        mutableStateOf<Long?>(null)
    }



    LaunchedEffect(userId) {

        RetrofitInstance.api.getUser(userId)
            .enqueue(object : Callback<User> {

                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {

                    if (response.isSuccessful) {

                        user = response.body()

                        RetrofitInstance.api.getImages(userId)
                            .enqueue(object : Callback<List<UserImage>> {

                                override fun onResponse(
                                    call: Call<List<UserImage>>,
                                    response: Response<List<UserImage>>
                                ) {

                                    loading = false

                                    if (response.isSuccessful) {

                                        images = response.body() ?: emptyList()

                                        println("Loaded user: ${user?.name}")
                                        println("Images: ${images.size}")
                                    }
                                    else {

                                        errorMessage = "Failed to load images"
                                    }
                                }

                                override fun onFailure(
                                    call: Call<List<UserImage>>,
                                    t: Throwable
                                ) {

                                    loading = false
                                    errorMessage = t.message
                                }
                            })
                    }
                    else {

                        loading = false
                        errorMessage = "Failed to load user"
                    }
                }

                override fun onFailure(
                    call: Call<User>,
                    t: Throwable
                ) {

                    loading = false
                    errorMessage = t.message
                }
            })
    }


    RetrofitInstance.api
        .getFriendshipStatus(currentUserId, userId)
        .enqueue(object : Callback<FriendshipStatusDto> {

            override fun onResponse(
                call: Call<FriendshipStatusDto>,
                response: Response<FriendshipStatusDto>
            ) {

                if (response.isSuccessful) {

                    response.body()?.let {

                        friendshipStatus = it.status
                        friendshipId = it.friendshipId
                        requestSenderId = it.senderId

                    }

                }

            }

            override fun onFailure(
                call: Call<FriendshipStatusDto>,
                t: Throwable
            ) { }

        })



    val profileImage = images.find {
        it.id == user?.profileImageId
    }

    fun formatDate(date: String?): String {

        if (date == null) return "Unknown"

        return try {

            val input =
                SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
                )

            val output =
                SimpleDateFormat(
                    "MMMM yyyy",
                    Locale.getDefault()
                )

            output.format(input.parse(date)!!)

        } catch (e: Exception) {

            date
        }
    }


    when {

        loading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        }

        errorMessage != null -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(errorMessage!!)
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AsyncImage(

                    model =
                        if (profileImage != null) {

                            if (profileImage.imagePath.startsWith("/uploads"))
                                "${RetrofitInstance.BASE_URL}${profileImage.imagePath}"
                            else
                                "${RetrofitInstance.BASE_URL}uploads/${profileImage.imagePath}"

                        } else {
                            null
                        },

                    contentDescription = null,

                    contentScale = ContentScale.Crop,

                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .border(
                            3.dp,
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = user!!.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "🌍 ${user!!.countryOfOrigin}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Joined since ${formatDate(user!!.createdAt)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                FriendshipButton(
                    friendshipStatus = friendshipStatus,
                    friendshipId = friendshipId,
                    requestSenderId = requestSenderId,
                    currentUserId = currentUserId!!,
                    viewedUserId = userId,
                    onStatusChanged = { newStatus ->
                        friendshipStatus = newStatus
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        if (currentUserId != null && userId != null) {

                            RetrofitInstance.api
                                .findExistingPrivateChat(userId)
                                .enqueue(object : Callback<PrivateChatLookupResponse> {

                                    override fun onResponse(
                                        call: Call<PrivateChatLookupResponse>,
                                        response: Response<PrivateChatLookupResponse>
                                    ) {

                                        if (response.isSuccessful) {

                                            response.body()?.let {

                                                if (it.exists) {

                                                    navController.navigate(
                                                        "chat/${it.chatId}"
                                                    )

                                                } else {

                                                    navController.navigate(
                                                        "privateChat/$userId"
                                                    )

                                                }
                                            }
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<PrivateChatLookupResponse>,
                                        t: Throwable
                                    ) {
                                        t.printStackTrace()
                                    }

                                })
                        }

                    }
                ) {
                    Text("Send Message")
                }

                Spacer(modifier = Modifier.height(16.dp))

                MutualFriendsSection(
                    currentUserId = currentUserId,
                    viewedUserId = userId,
                    navController = navController
                )




                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Bio",
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text =
                                if (user!!.bio.isNullOrBlank())
                                    "This user hasn't written a bio yet."
                                else
                                    user!!.bio!!
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Photos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(12.dp))

                if (images.isEmpty()) {

                    Text("No photos uploaded")

                } else {

                    LazyVerticalGrid(

                        columns = GridCells.Fixed(2),

                        modifier = Modifier
                            .fillMaxWidth()
                            .height((images.size / 2 + images.size % 2) * 190.dp),

                        horizontalArrangement = Arrangement.spacedBy(8.dp),

                        verticalArrangement = Arrangement.spacedBy(8.dp)

                    ) {

                        items(images) { image ->

                            Card {

                                AsyncImage(

                                    model =
                                        if (image.imagePath.startsWith("/uploads"))
                                            "${RetrofitInstance.BASE_URL}${image.imagePath}"
                                        else
                                            "${RetrofitInstance.BASE_URL}uploads/${image.imagePath}",

                                    contentDescription = null,

                                    contentScale = ContentScale.Crop,

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

        }
    }
}