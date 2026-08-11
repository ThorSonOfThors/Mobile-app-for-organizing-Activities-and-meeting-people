package com.example.myapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.components.FriendRequestRow
import com.example.myapplication.components.FriendRow
import com.example.myapplication.models.Friend
import com.example.myapplication.models.FriendRequest
import com.example.myapplication.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    userId: Long,
    navController: NavController
) {

    var friends by remember {
        mutableStateOf<List<Friend>>(emptyList())
    }

    var pendingRequests by remember {
        mutableStateOf<List<FriendRequest>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var friendsExpanded by remember {
        mutableStateOf(true)
    }

    var requestsExpanded by remember {
        mutableStateOf(true)
    }

    fun loadData() {

        loading = true

        RetrofitInstance.api.getFriends(userId)
            .enqueue(object : Callback<List<Friend>> {

                override fun onResponse(
                    call: Call<List<Friend>>,
                    response: Response<List<Friend>>
                ) {

                    if (response.isSuccessful) {
                        friends = response.body() ?: emptyList()
                    }

                    loading = false
                }

                override fun onFailure(
                    call: Call<List<Friend>>,
                    t: Throwable
                ) {
                    loading = false
                }
            })

        RetrofitInstance.api.getPendingRequests(userId)
            .enqueue(object : Callback<List<FriendRequest>> {

                override fun onResponse(
                    call: Call<List<FriendRequest>>,
                    response: Response<List<FriendRequest>>
                ) {

                    if (response.isSuccessful) {
                        pendingRequests = response.body() ?: emptyList()
                    }

                }

                override fun onFailure(
                    call: Call<List<FriendRequest>>,
                    t: Throwable
                ) {
                }

            })

    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Friends")
                }

            )

        }

    ) { padding ->

        if (loading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()

            }

        } else {

            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                item {

                    CollapsibleHeader(

                        title = "Friend Requests",

                        count = pendingRequests.size,

                        expanded = requestsExpanded

                    ) {

                        requestsExpanded = !requestsExpanded

                    }

                }

                if (requestsExpanded) {

                    items(
                        pendingRequests,
                        key = { it.friendshipId }
                    ) { request ->

                        FriendRequestRow(

                            request = request,
                            currentUserId = userId,
                            navController = navController,

                            onUpdated = {
                                loadData()
                            }

                        )

                    }

                }

                item {

                    Spacer(Modifier.height(16.dp))

                }

                item {

                    CollapsibleHeader(

                        title = "Friends",

                        count = friends.size,

                        expanded = friendsExpanded

                    ) {

                        friendsExpanded = !friendsExpanded

                    }

                }

                if (friendsExpanded) {

                    items(
                        friends,
                        key = { it.friendshipId }
                    ) { friend ->

                        FriendRow(

                            friend = friend,
                            currentUserId = userId,
                            navController = navController,

                            onRemoved = {
                                loadData()
                            }

                        )

                    }

                }

            }

        }

    }

}

@Composable
fun CollapsibleHeader(

    title: String,

    count: Int,

    expanded: Boolean,

    onClick: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween

        ) {

            Text(

                "$title ($count)",

                fontWeight = FontWeight.Bold

            )

            Text(

                if (expanded) "▲" else "▼"

            )

        }

    }

}