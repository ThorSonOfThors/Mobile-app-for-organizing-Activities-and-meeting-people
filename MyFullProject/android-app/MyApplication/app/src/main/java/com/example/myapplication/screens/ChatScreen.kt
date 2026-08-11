package com.example.myapplication.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.models.ChatHeaderResponse
import com.example.myapplication.models.MessageDto
import com.example.myapplication.models.Participant
import com.example.myapplication.models.SendMessageRequest
import com.example.myapplication.network.RetrofitInstance

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    chatId: Long,
    userId: Long,
    navController: NavController
) {
    var activityTitle by remember { mutableStateOf("") }
    var participantCount by remember { mutableStateOf(0) }
    var participants by remember { mutableStateOf<List<Participant>>(emptyList()) }
    var headerLoading by remember { mutableStateOf(true) }

    var messages by remember { mutableStateOf<List<MessageDto>>(emptyList()) }
    var input by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<MessageDto?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var isGroup by remember {
        mutableStateOf(true)
    }

    var otherUserId by remember {
        mutableStateOf<Long?>(null)
    }

    var otherUserName by remember {
        mutableStateOf("")
    }

    var otherUserImage by remember {
        mutableStateOf<Long?>(null)
    }


    val messageIndexMap = remember(messages) {
        messages.withIndex().associate { it.value.id to it.index }
    }

    var highlightedMessageId by remember { mutableStateOf<Long?>(null) }

    // Helper function to get profile image URL
    fun getProfileImageUrl(profileImageId: Long?): String? {
        return if (profileImageId != null) {
            "${RetrofitInstance.BASE_URL}api/users/profile-image/$profileImageId"
        } else {
            null
        }
    }

    // Format timestamp to show only time (HH:mm)
    fun formatTimeOnly(timestamp: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(timestamp)
            if (date != null) {
                outputFormat.format(date)
            } else {
                timestamp
            }
        } catch (e: Exception) {
            timestamp
        }
    }

    // Load chat header
    LaunchedEffect(chatId) {
        RetrofitInstance.api.getChatHeader(chatId)
            .enqueue(object : Callback<ChatHeaderResponse> {
                override fun onResponse(
                    call: Call<ChatHeaderResponse>,
                    response: Response<ChatHeaderResponse>
                ) {

                    Log.d(
                        "CHAT_HEADER_DEBUG",
                        "Response received. chatId=$chatId success=${response.isSuccessful} code=${response.code()}"
                    )

                    headerLoading = false

                    if (response.isSuccessful) {

                        response.body()?.let { header ->

                            Log.d(
                                                    "CHAT_HEADER_DEBUG",
                                                    """
                            isGroup=${header.isGroup}
                            
                            otherUserId=${header.otherUserId}
                            otherUserName=${header.otherUserName}
                            image=${header.otherUserProfileImageId}
                            activity=${header.activityTitle}
                            participants=${header.participantCount}
                            """.trimIndent()
                            )


                            isGroup = header.isGroup

                            participantCount = header.participantCount

                            participants = header.participants ?: emptyList()


                            otherUserId = header.otherUserId

                            otherUserName = header.otherUserName ?: ""

                            otherUserImage = header.otherUserProfileImageId


                            activityTitle = header.activityTitle ?: ""

                        }

                    } else {

                        Log.e(
                            "CHAT_HEADER_DEBUG",
                            "ERROR BODY=${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<ChatHeaderResponse>, t: Throwable) {
                    headerLoading = false
                    t.printStackTrace()
                }
            })
    }

    // Load messages
    LaunchedEffect(chatId) {
        RetrofitInstance.api.getMessages(chatId)
            .enqueue(object : Callback<List<MessageDto>> {
                override fun onResponse(
                    call: Call<List<MessageDto>>,
                    response: Response<List<MessageDto>>
                ) {
                    if (response.isSuccessful) {
                        messages = response.body() ?: emptyList()
                        RetrofitInstance.api.markMessagesSeen(chatId, userId)
                    }
                }

                override fun onFailure(call: Call<List<MessageDto>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    // Mark messages as seen
    LaunchedEffect(chatId) {
        RetrofitInstance.api.markMessagesSeen(chatId, userId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })
    }

    // Auto-scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    if (headerLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        //---------------------------------------------------------
        // Activity Navbar
        //---------------------------------------------------------
        //---------------------------------------------------------
        // Chat Navbar
        //--------------------------------------------------------

        //---------------------------------------------------------
        // Chat Navbar
        //---------------------------------------------------------

        if (isGroup) {

            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        otherUserId?.let {
                            navController.navigate("userProfile/$it")
                        }
                    }
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val imageUrl = getProfileImageUrl(otherUserImage)

                    if (imageUrl != null) {

                        AsyncImage(
                            model = imageUrl,
                            contentDescription = otherUserName,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = otherUserName
                                        .firstOrNull()
                                        ?.toString()
                                        ?: "?"
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = otherUserName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        } else {

            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("activityDetails/$chatId")
                    }
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = activityTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "$participantCount participants",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        participants.take(7).forEach { participant ->

                            val profileImageUrl =
                                getProfileImageUrl(participant.profileImageId)

                            if (profileImageUrl != null) {

                                AsyncImage(
                                    model = profileImageUrl,
                                    contentDescription = participant.name,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                            } else {

                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = participant.name.first().toString()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        //---------------------------------------------------------
        // Chat Messages
        //---------------------------------------------------------
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            state = listState,
            reverseLayout = false
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                val mine = message.senderId == userId
                val highlighted = highlightedMessageId == message.id
                val timeOnly = formatTimeOnly(message.sentAt)

                val currentIndex = messages.indexOf(message)
                val previousMessage = if (currentIndex > 0) messages[currentIndex - 1] else null
                val isFirstInSequence =
                    previousMessage == null || previousMessage.senderId != message.senderId

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .pointerInput(message.id) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, dragAmount ->
                                    if (dragAmount > 60) {
                                        replyingTo = message
                                    }
                                }
                            )
                        },
                    horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    // Profile image (only for incoming messages and first in sequence)
                    if (!mine) {
                        if (isFirstInSequence) {
                            val profileImageUrl = getProfileImageUrl(
                                participants.find { it.id == message.senderId }?.profileImageId
                            )

                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable {
                                        navController.navigate("userProfile/${message.senderId}")
                                    }
                            ) {
                                if (profileImageUrl != null) {
                                    AsyncImage(
                                        model = profileImageUrl,
                                        contentDescription = message.senderName,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier.size(32.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = message.senderName.first().toString(),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.width(40.dp))
                        }
                    }

                    Column(
                        horizontalAlignment = if (mine) Alignment.End else Alignment.Start,
                        modifier = Modifier.fillMaxWidth(if (mine) 1f else 0.85f)
                    ) {
                        // Show sender name only for first message in sequence (incoming)
                        if (!mine && isFirstInSequence) {
                            Text(
                                text = message.senderName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        // Reply preview card
                        if (message.replyToMessageId != null) {
                            Card(
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .clickable {
                                        val repliedId = message.replyToMessageId
                                        val index = messageIndexMap[repliedId]
                                        if (index != null) {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(index)
                                                highlightedMessageId = repliedId
                                                delay(1500)
                                                highlightedMessageId = null
                                            }
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = 0.6f
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .widthIn(max = 250.dp)
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "↩ ${message.replySenderName ?: "Unknown"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = message.replyPreview ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Message bubble with correct corner shapes
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = if (mine) {
                                    // For user's messages: top-left is rounded
                                    16.dp
                                } else {
                                    // For other users' messages: top-left is flat (sharp edge)
                                    4.dp
                                },
                                topEnd = if (mine) {
                                    // For user's messages: top-right is flat (sharp edge)
                                    4.dp
                                } else {
                                    // For other users' messages: top-right is rounded
                                    16.dp
                                },
                                bottomStart = if (mine) 16.dp else 4.dp,
                                bottomEnd = if (mine) 16.dp else 16.dp
                            ),
                            color = when {
                                highlighted -> Color.Yellow.copy(alpha = 0.3f)
                                mine -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            },
                            shadowElevation = if (highlighted) 4.dp else 0.dp,
                            modifier = Modifier
                                .wrapContentWidth()
                                .widthIn(min = 50.dp, max = 280.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                // Message text
                                Text(
                                    text = message.content,
                                    color = when {
                                        highlighted -> MaterialTheme.colorScheme.onSurface
                                        mine -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                // Time and status - bottom right for all messages
                                Row(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .align(Alignment.End)
                                        .padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = timeOnly,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (mine) {
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                        } else {
                                            MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                                alpha = 0.6f
                                            )
                                        },
                                        fontSize = 9.sp
                                    )

                                    // Show status only for outgoing messages
                                    if (mine) {
                                        Spacer(Modifier.width(3.dp))

                                        when (message.status) {
                                            "sending" -> {
                                                Text(
                                                    text = "✓",
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                        alpha = 0.4f
                                                    ),
                                                    fontSize = 9.sp,
                                                    letterSpacing = 0.sp
                                                )
                                            }

                                            "sent" -> {
                                                Text(
                                                    text = "✓✓",
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                        alpha = 0.6f
                                                    ),
                                                    fontSize = 9.sp,
                                                    letterSpacing = (-0.5).sp
                                                )
                                            }

                                            "seen" -> {
                                                Text(
                                                    text = "✓✓",
                                                    color = Color(0xFF4CAF50),
                                                    fontSize = 9.sp,
                                                    letterSpacing = (-0.5).sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //---------------------------------------------------------
        // Reply Indicator
        //---------------------------------------------------------
        replyingTo?.let { reply ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable {
                        val index = messageIndexMap[reply.id]
                        if (index != null) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                                highlightedMessageId = reply.id
                                delay(1500)
                                highlightedMessageId = null
                            }
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Replying to ${reply.senderName}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = reply.content,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { replyingTo = null }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel reply",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        //---------------------------------------------------------
        // Send Message Bar
        //---------------------------------------------------------
        Surface(
            tonalElevation = 4.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = input,
                    onValueChange = { input = it },
                    placeholder = {
                        Text(
                            text = "Type a message...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.2f
                        ),
                    ),
                    singleLine = true,
                    maxLines = 5,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                FloatingActionButton(
                    onClick = {
                        if (input.isBlank()) return@FloatingActionButton

                        val request = SendMessageRequest(
                            senderId = userId,
                            content = input,
                            replyToMessageId = replyingTo?.id,
                            replyPreview = replyingTo?.content,
                            replySenderName = replyingTo?.senderName
                        )

                        RetrofitInstance.api.sendMessage(chatId, request)
                            .enqueue(object : Callback<MessageDto> {
                                override fun onResponse(
                                    call: Call<MessageDto>,
                                    response: Response<MessageDto>
                                ) {
                                    if (response.isSuccessful) {
                                        response.body()?.let {
                                            replyingTo = null
                                            messages = messages + it
                                        }
                                        input = ""
                                    }
                                }

                                override fun onFailure(call: Call<MessageDto>, t: Throwable) {
                                    t.printStackTrace()
                                }
                            })
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send message"
                    )
                }
            }
        }
    }
}


