package com.example.myapplication.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.models.User
import com.example.myapplication.network.RetrofitInstance
import com.example.myapplication.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.models.UserImage

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.layout.ContentScale

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController

@Composable
fun ProfileScreen(
    currentUserId: Long?,
    navController: NavController
) {

    val userId = SessionManager.currentUserId

    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    var uploadingProfileImage by remember {
        mutableStateOf(false)
    }

    var createdAt by remember {
        mutableStateOf("")
    }


    val context = LocalContext.current

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var images by remember {
        mutableStateOf<List<UserImage>>(
            emptyList()
        )
    }
    var profileImageId by remember {
        mutableStateOf<Long?>(null)
    }

    var statusMessage by remember {
        mutableStateOf("")
    }

    val profileImage = images.find {
        it.id == profileImageId
    }

    LaunchedEffect(images, profileImageId) {

        android.util.Log.d(
            "PROFILE_DEBUG",
            "profileImageId=$profileImageId"
        )

        android.util.Log.d(
            "PROFILE_DEBUG",
            "profileImage=$profileImage"
        )

        profileImage?.let {

            android.util.Log.d(
                "PROFILE_DEBUG",
                "profile path=${it.imagePath}"
            )

            android.util.Log.d(
                "PROFILE_DEBUG",
                "full url=http://192.168.100.50:8081/uploads/${it.imagePath}"
            )
        }
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.GetContent()
        ) { uri ->

            selectedImageUri = uri

            if(uri != null && userId != null) {

                try {

                    val inputStream =
                        context.contentResolver
                            .openInputStream(uri)

                    val tempFile =
                        File.createTempFile(
                            "upload",
                            ".jpg",
                            context.cacheDir
                        )

                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    val requestFile =
                        tempFile
                            .asRequestBody(
                                "image/*"
                                    .toMediaTypeOrNull()
                            )

                    val body =
                        MultipartBody.Part
                            .createFormData(
                                "file",
                                tempFile.name,
                                requestFile
                            )

                    RetrofitInstance.api
                        .uploadImage(
                            userId,
                            body
                        )
                        .enqueue(
                            object :
                                Callback<UserImage> {

                                override fun onResponse(
                                    call: Call<UserImage>,
                                    response: Response<UserImage>
                                ) {

                                    if (response.isSuccessful) {

                                        val uploadedImage = response.body()

                                        if (uploadedImage != null && userId != null && uploadingProfileImage) {

                                            // 1. Update local profile immediately (UI update)
                                            profileImageId = uploadedImage.id

                                            // 2. Persist to backend user
                                            RetrofitInstance.api.updateUser(
                                                userId,
                                                User(
                                                    id = userId,
                                                    name = name,
                                                    countryOfOrigin = country,
                                                    email = email,
                                                    bio = bio,
                                                    profileImageId = uploadedImage.id,
                                                )
                                            ).enqueue(object : Callback<User> {

                                                override fun onResponse(
                                                    call: Call<User>,
                                                    response: Response<User>
                                                ) {
                                                    statusMessage = "Profile image updated"
                                                }

                                                override fun onFailure(call: Call<User>, t: Throwable) {
                                                    statusMessage = "Profile update failed"
                                                }
                                            })
                                        }

                                        // refresh images list (existing logic)
                                        RetrofitInstance.api.getImages(userId)
                                            .enqueue(object : Callback<List<UserImage>> {
                                                override fun onResponse(
                                                    call: Call<List<UserImage>>,
                                                    response: Response<List<UserImage>>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        images = response.body() ?: emptyList()
                                                    }
                                                }

                                                override fun onFailure(call: Call<List<UserImage>>, t: Throwable) {}
                                            })
                                    }
                                }

                                override fun onFailure(
                                    call: Call<UserImage>,
                                    t: Throwable
                                ) {

                                    statusMessage =
                                        "Upload failed: ${t.message}"
                                }
                            }
                        )

                } catch(e: Exception) {

                    statusMessage =
                        "Upload failed: ${e.message}"
                }
            }
        }


    LaunchedEffect(Unit) {

        if(userId != null) {

            RetrofitInstance.api
                .getUser(userId)
                .enqueue(
                    object : Callback<User> {

                        override fun onResponse(
                            call: Call<User>,
                            response: Response<User>
                        ) {

                            if(response.isSuccessful) {

                                response.body()?.let {

                                    name = it.name
                                    country = it.countryOfOrigin
                                    email = it.email
                                    bio = it.bio ?: ""
                                    profileImageId = it.profileImageId

                                    android.util.Log.d(
                                        "PROFILE_DEBUG",
                                        "profileImageId=$profileImageId"
                                    )

                                    // NOW fetch images
                                    RetrofitInstance.api
                                        .getImages(userId)
                                        .enqueue(
                                            object : Callback<List<UserImage>> {

                                                override fun onResponse(
                                                    call: Call<List<UserImage>>,
                                                    response: Response<List<UserImage>>
                                                ) {

                                                    if(response.isSuccessful) {

                                                        images =
                                                            response.body()
                                                                ?: emptyList()

                                                        images.forEach {
                                                            android.util.Log.d(
                                                                "PROFILE_DEBUG",
                                                                "image id=${it.id}, path=${it.imagePath}"
                                                            )
                                                        }

                                                        android.util.Log.d(
                                                            "PROFILE_DEBUG",
                                                            "profileImageId=$profileImageId"
                                                        )


                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<List<UserImage>>,
                                                    t: Throwable
                                                ) {
                                                }
                                            }
                                        )
                                }
                            }
                        }

                        override fun onFailure(
                            call: Call<User>,
                            t: Throwable
                        ) {

                            statusMessage =
                                "Failed to load profile"
                        }
                    }
                )
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(16.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(12.dp)
        )

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

            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    shape = CircleShape,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {
                uploadingProfileImage = true
                imagePickerLauncher.launch(
                    "image/*"
                )
            }
        ) {
            Text("Change Profile Picture")
        }


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate("friends/$currentUserId")
            }
        ) {
            Text("Friend Requests")
        }



        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Name")
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = {
                        country = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Country")
                    }
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "🌍 $country"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = {
                        bio = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    minLines = 4,

                    label = {
                        Text("Bio")
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text("Email")
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    modifier =
                        Modifier.fillMaxWidth(),

                    onClick = {

                        if(userId != null) {

                            RetrofitInstance.api
                                .updateUser(
                                    userId,

                                    User(
                                        id = userId,
                                        name = name,
                                        countryOfOrigin = country,
                                        email = email,
                                        bio = bio,
                                        profileImageId = profileImageId,
                                    )
                                )
                                .enqueue(
                                    object :
                                        Callback<User> {

                                        override fun onResponse(
                                            call: Call<User>,
                                            response: Response<User>
                                        ) {

                                            statusMessage =
                                                "Profile saved"
                                        }

                                        override fun onFailure(
                                            call: Call<User>,
                                            t: Throwable
                                        ) {

                                            statusMessage =
                                                "Failed to save profile"
                                        }
                                    }
                                )
                        }
                    }
                ) {

                    Text("Save Profile")
                }
            }
        }

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(statusMessage)

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text("My Photos")



        Button(
            onClick = {
                uploadingProfileImage = false
                imagePickerLauncher.launch("image/*")
            }
        ) {
            Text("Add Photo")
        }



        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(images) { image ->

                Box {

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

                    IconButton(
                        onClick = {

                            if (userId != null) {

                                RetrofitInstance.api
                                    .deleteImage(
                                        userId,
                                        image.id
                                    )
                                    .enqueue(
                                        object : Callback<Void> {

                                            override fun onResponse(
                                                call: Call<Void>,
                                                response: Response<Void>
                                            ) {

                                                if (response.isSuccessful) {

                                                    // If deleting profile image, clear it
                                                    if (image.id == profileImageId) {

                                                        profileImageId = null

                                                        RetrofitInstance.api.updateUser(
                                                            userId,
                                                            User(
                                                                id = userId,
                                                                name = name,
                                                                countryOfOrigin = country,
                                                                email = email,
                                                                bio = bio,
                                                                profileImageId = null,
                                                            )
                                                        ).enqueue(object : Callback<User> {

                                                            override fun onResponse(
                                                                call: Call<User>,
                                                                response: Response<User>
                                                            ) {
                                                            }

                                                            override fun onFailure(
                                                                call: Call<User>,
                                                                t: Throwable
                                                            ) {
                                                            }
                                                        })
                                                    }

                                                    images =
                                                        images.filter {
                                                            it.id != image.id
                                                        }
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<Void>,
                                                t: Throwable
                                            ) {

                                                statusMessage =
                                                    "Failed to delete image"
                                            }
                                        }
                                    )
                            }
                        },

                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.Black.copy(alpha = 0.5f)
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete image",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }


    }
}