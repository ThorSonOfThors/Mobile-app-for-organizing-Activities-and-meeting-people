package com.example.myapplication.screens

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.models.Activity
import com.example.myapplication.models.CreateActivityRequest
import com.example.myapplication.network.RetrofitInstance
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import androidx.compose.foundation.layout.safeDrawingPadding
import com.example.myapplication.components.NotificationBell
import com.example.myapplication.models.Notification

import androidx.navigation.NavController

import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay



// ---------------- MODELS ----------------



// ---------------- SCREEN ----------------

@Composable
fun HomeScreen(
    userId: Long?,
    navController: NavController) {


    var activities by remember { mutableStateOf<List<Activity>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    var notifications by remember {
        mutableStateOf<List<Notification>>(emptyList())
    }


    val context = LocalContext.current

    var selectedActivity by remember { mutableStateOf<Activity?>(null) }



    var selectedLocation by remember {
        mutableStateOf<GeoPoint?>(null)
    }

    // load activities
    LaunchedEffect(Unit) {
        RetrofitInstance.api.getActivities()
            .enqueue(object : Callback<List<Activity>> {
                override fun onResponse(
                    call: Call<List<Activity>>,
                    response: Response<List<Activity>>
                ) {
                    if (response.isSuccessful) {
                        activities = response.body() ?: emptyList()
                    }
                }

                override fun onFailure(call: Call<List<Activity>>, t: Throwable) {}
            })

        if (userId != null) {

            RetrofitInstance.api
                .getNotifications(userId)
                .enqueue(object : Callback<List<Notification>> {

                    override fun onResponse(
                        call: Call<List<Notification>>,
                        response: Response<List<Notification>>
                    ) {

                        if (response.isSuccessful) {

                            notifications =
                                response.body() ?: emptyList()
                        }
                    }

                    override fun onFailure(
                        call: Call<List<Notification>>,
                        t: Throwable
                    ) {
                        t.printStackTrace()
                    }
                })
        }
    }





    Box(modifier = Modifier.fillMaxSize()) {

        // ---------------- MAP (OSM) ----------------

        AndroidView(
            factory = { ctx ->

                Configuration.getInstance().userAgentValue =
                    "MyApplication/1.0 (contact: filintros@gmail.com)"


                Configuration.getInstance().load(
                    ctx,
                    ctx.getSharedPreferences("osm", Context.MODE_PRIVATE)
                )



                Configuration.getInstance().isDebugMode = false
                Configuration.getInstance().isDebugTileProviders = false
                Configuration.getInstance().isDebugMapTileDownloader = false

                MapView(ctx).apply {

                    setTileSource(TileSourceFactory.OpenTopo)

                    setMinimumHeight(1)
                    setTileSource(TileSourceFactory.OpenTopo)

                    setMultiTouchControls(true)

                    controller.setZoom(12.0)
                    controller.setCenter(
                        GeoPoint(-6.2088, 106.8456) // Jakarta
                    )
                }
            },
            update = { mapView ->

                mapView.overlays.clear()


                activities.forEach { activity ->

                    val marker = Marker(mapView)
                    marker.position = GeoPoint(
                        activity.latitude,
                        activity.longitude
                    )

                    marker.title = activity.title
                    marker.snippet = activity.description

                    marker.setOnMarkerClickListener { _, _ ->
                        selectedActivity = activity
                        true
                    }

                    mapView.overlays.add(marker)
                }


                mapView.invalidate()
            }
        )

        // ---------------- NOTIFICATIONS ----------------

        NotificationBell(
            notifications = notifications,

            onNotificationClicked = { notification: Notification ->

                if (userId != null) {

                    // Mark notification as seen locally immediately
                    notifications = notifications.map { currentNotification: Notification ->

                        if (
                            currentNotification.notificationId ==
                            notification.notificationId
                        ) {
                            currentNotification.copy(seen = true)
                        } else {
                            currentNotification
                        }
                    }

                    // Mark notification as seen on backend
                    RetrofitInstance.api
                        .markNotificationAsSeen(
                            notification.notificationId,
                            userId
                        )
                        .enqueue(object : Callback<Void> {

                            override fun onResponse(
                                call: Call<Void>,
                                response: Response<Void>
                            ) {
                                if (!response.isSuccessful) {
                                    println(
                                        "Failed to mark notification as seen: ${response.code()}"
                                    )
                                }
                            }

                            override fun onFailure(
                                call: Call<Void>,
                                t: Throwable
                            ) {
                                t.printStackTrace()
                            }
                        })

                    // Navigate according to notification type
                    when (notification.type) {

                        "FRIEND_REQUEST_RECEIVED",
                        "FRIEND_REQUEST_ACCEPTED" -> {

                            notification.actorUserId?.let { targetUserId: Long ->

                                navController.navigate(
                                    "userProfile/$targetUserId"
                                )
                            }
                        }

                        "ACTIVITY_JOINED" -> {

                            println(
                                "ACTIVITY_JOINED notification: " +
                                        "notificationId=${notification.notificationId}, " +
                                        "activityId=${notification.activityId}, " +
                                        "chatId=${notification.chatId}, " +
                                        "actorUserId=${notification.actorUserId}"
                            )

                            val chatId = notification.chatId

                            if (chatId != null) {

                                navController.navigate(
                                    "chat/$chatId"
                                )

                            } else {

                                println(
                                    "ERROR: ACTIVITY_JOINED notification has NULL chatId"
                                )
                            }
                        }

                        "NEW_MESSAGE" -> {

                            notification.chatId?.let { chatId: Long ->

                                navController.navigate(
                                    "chat/$chatId"
                                )
                            }
                        }
                    }
                }
            },

            onMarkAllAsSeen = {

                if (userId != null) {

                    // Update UI immediately
                    notifications = notifications.map { notification: Notification ->
                        notification.copy(seen = true)
                    }

                    // Update backend
                    RetrofitInstance.api
                        .markAllNotificationsAsSeen(userId)
                        .enqueue(object : Callback<Void> {

                            override fun onResponse(
                                call: Call<Void>,
                                response: Response<Void>
                            ) {
                                if (!response.isSuccessful) {
                                    println(
                                        "Failed to mark all notifications as seen: ${response.code()}"
                                    )
                                }
                            }

                            override fun onFailure(
                                call: Call<Void>,
                                t: Throwable
                            ) {
                                t.printStackTrace()
                            }
                        })
                }
            },

            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // ---------------- FAB ----------------

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {

            FloatingActionButton(
                onClick = {

                    selectedLocation = GeoPoint(
                        -6.2188,
                        106.8456
                    )

                    showDialog = true
                }
            ) {
                Text("+")
            }

        }
    }

    // ---------------- DIALOG ----------------

    if (showDialog && selectedLocation != null) {
        CreateActivityDialog(
            userId = userId,
            latitude = selectedLocation!!.latitude,
            longitude = selectedLocation!!.longitude,
            onDismiss = { showDialog = false },
            onCreated = { newActivity ->
                activities = activities + newActivity
            }
        )
    }

    selectedActivity?.let { activity ->

        AlertDialog(
            onDismissRequest = {
                selectedActivity = null
            },

            title = {
                Text(activity.creatorName +" wants to " +  activity.title)
            },

            text = {
                Column {

                    Text(activity.description)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Time: ${activity.eventTime}")
                    //Text("Longitude: ${activity.longitude}")
                }
            },

            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    ),
                    onClick = {

                        if (userId != null) {

                            RetrofitInstance.api.joinActivity(
                                activity.activityId,
                                userId
                            ).enqueue(object : Callback<Activity> {

                                override fun onResponse(
                                    call: Call<Activity>,
                                    response: Response<Activity>
                                ) {
                                    if (response.isSuccessful) {
                                        selectedActivity = null
                                    }
                                }

                                override fun onFailure(
                                    call: Call<Activity>,
                                    t: Throwable
                                ) {
                                    // Handle failure if desired
                                }
                            })
                        }
                    }
                ) {
                    Text("Join Activity")
                }
            },

            dismissButton = {
                Button(
                    onClick = {
                        selectedActivity = null
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}

// ---------------- DIALOG ----------------

@Composable
fun CreateActivityDialog(
    userId: Long?,
    latitude: Double,
    longitude: Double,
    onDismiss: () -> Unit,
    onCreated: (Activity) -> Unit
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var pickedLocation by remember {
        mutableStateOf(
            GeoPoint(latitude, longitude)
        )
    }

    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    val context = LocalContext.current

    val timePicker = remember {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                selectedTime = LocalTime.of(hour, minute)
            },
            12,
            0,
            true
        )
    }

    Dialog(

        onDismissRequest = onDismiss,

        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )

    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {


                // -------- TOP FORM --------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .clipToBounds(),
                ) {


                    Text(
                        text = "Create Activity",
                        style = MaterialTheme.typography.headlineSmall
                    )


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        label = {
                            Text("Title")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        label = {
                            Text("Description")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Button(
                        onClick = {
                            timePicker.show()
                        }
                    ) {

                        Text(
                            selectedTime?.toString()
                                ?: "Pick time"
                        )

                    }


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Text(
                        text =
                            "Location: %.5f , %.5f"
                                .format(
                                    pickedLocation.latitude,
                                    pickedLocation.longitude
                                )
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Button(
                        onClick = {

                            if (userId == null)
                                return@Button


                            val request =
                                CreateActivityRequest(
                                    creatorId = userId!!,
                                    title = title,
                                    description = description,
                                    latitude = pickedLocation.latitude,
                                    longitude = pickedLocation.longitude,
                                    eventTime = LocalDateTime.of(
                                        LocalDate.now(),
                                        selectedTime ?: LocalTime.NOON
                                    ).toString()
                                )


                            RetrofitInstance.api
                                .createActivity(request)
                                .enqueue(object :
                                    Callback<Activity> {

                                    override fun onResponse(
                                        call: Call<Activity>,
                                        response: Response<Activity>
                                    ) {

                                        if (response.isSuccessful) {

                                            response.body()
                                                ?.let {
                                                    onCreated(it)
                                                }

                                            onDismiss()
                                        }
                                    }


                                    override fun onFailure(
                                        call: Call<Activity>,
                                        t: Throwable
                                    ) {

                                    }

                                })

                        }
                    ) {

                        Text("Create")

                    }

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clipToBounds()
                ) {

                    AndroidView(

                        modifier = Modifier.fillMaxSize(),

                        factory = { ctx ->


                            MapView(ctx).apply {


                                setTileSource(
                                    TileSourceFactory.OpenTopo
                                )

                                setMultiTouchControls(true)


                                controller.setZoom(15.0)

                                controller.setCenter(
                                    pickedLocation
                                )


                                val marker =
                                    Marker(this)


                                marker.position =
                                    pickedLocation


                                marker.title =
                                    "Selected location"


                                overlays.add(marker)


                                val receiver =
                                    object :
                                        MapEventsReceiver {


                                        override fun singleTapConfirmedHelper(
                                            p: GeoPoint
                                        ): Boolean {


                                            pickedLocation =
                                                p


                                            marker.position =
                                                p


                                            invalidate()


                                            return true
                                        }


                                        override fun longPressHelper(
                                            p: GeoPoint
                                        ): Boolean {

                                            return false
                                        }

                                    }



                                overlays.add(
                                    MapEventsOverlay(receiver)
                                )

                            }

                        },


                        update = { map ->


                            map.invalidate()

                        }

                    )

                }

            }

        }
    }
}

