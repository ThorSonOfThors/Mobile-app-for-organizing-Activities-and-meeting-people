package com.example.myapplication.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.example.myapplication.network.RetrofitInstance

@Composable
fun ProfileImage(
    imageId: Long?,
    size: Dp,
    modifier: Modifier = Modifier
) {

    AsyncImage(

        model = if (imageId != null)
            "${RetrofitInstance.BASE_URL}api/users/profile-image/$imageId"
        else
            null,

        contentDescription = null,

        modifier = modifier.size(size),

        contentScale = ContentScale.Crop

    )

}