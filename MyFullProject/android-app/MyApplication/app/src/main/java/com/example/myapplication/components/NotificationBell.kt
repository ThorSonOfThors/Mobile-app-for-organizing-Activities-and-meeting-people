package com.example.myapplication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DropdownMenu
import com.example.myapplication.models.Notification

@Composable
fun NotificationBell(
    notifications: List<Notification>,
    onNotificationClicked: (Notification) -> Unit,
    onMarkAllAsSeen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    // Explicitly typed so Kotlin has no inference problem.
    val sortedNotifications: List<Notification> =
        remember(notifications) {
            notifications.sortedByDescending { notification: Notification ->
                notification.createdAt
            }
        }

    val hasUnread: Boolean =
        notifications.any { notification: Notification ->
            !notification.seen
        }

    Box(
        modifier = modifier
    ) {

        // Bell
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    expanded = !expanded
                },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                modifier = Modifier.size(26.dp)
            )

            // Red unread dot
            if (hasUnread) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = Color.Red,
                            shape = CircleShape
                        )
                        .align(Alignment.TopEnd)
                )
            }
        }

        // Popup
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (hasUnread) {
                    Text(
                        text = "Mark all as seen",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            onMarkAllAsSeen()
                        }
                    )
                }
            }

            HorizontalDivider()

            // Empty state
            if (sortedNotifications.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Gray
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "No notifications",
                        color = Color.Gray
                    )
                }

            } else {

                // Notifications
                LazyColumn(
                    modifier = Modifier
                        .height(450.dp)
                        .width(350.dp)
                ) {

                    items(
                        items = sortedNotifications,
                        key = { notification: Notification ->
                            notification.notificationId
                        }
                    ) { notification: Notification ->

                        NotificationItem(
                            notification = notification,
                            onClick = {
                                onNotificationClicked(notification)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {

    val backgroundColor: Color =
        if (!notification.seen) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.Top
    ) {

        // Notification icon
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        // Text
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = notification.title,
                fontSize = 14.sp,
                fontWeight =
                    if (!notification.seen) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
            )

            if (!notification.message.isNullOrBlank()) {

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = notification.message ?: "",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = notification.createdAt,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        // Individual unread indicator
        if (!notification.seen) {

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }

    HorizontalDivider()
}