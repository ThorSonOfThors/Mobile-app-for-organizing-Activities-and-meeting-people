package com.example.myapplication.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.example.myapplication.network.RetrofitInstance

@Composable
fun PrivateChatTopBar(
    userId: Long,
    userName: String,
    profileImageId: Long?,
    navController: NavController
) {

    fun getProfileImageUrl(
        imageId: Long?
    ): String? {

        return if(imageId != null) {
            "${RetrofitInstance.BASE_URL}api/users/profile-image/$imageId"
        } else {
            null
        }

    }


    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                navController.navigate(
                    "userProfile/$userId"
                )

            }
    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),

            verticalAlignment = Alignment.CenterVertically

        ) {


            val imageUrl =
                getProfileImageUrl(profileImageId)



            if(imageUrl != null) {


                AsyncImage(

                    model = imageUrl,

                    contentDescription = userName,

                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),

                    contentScale = ContentScale.Crop
                )


            } else {


                Surface(

                    modifier = Modifier.size(42.dp),

                    shape = CircleShape,

                    color = MaterialTheme.colorScheme.primaryContainer

                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = userName
                                .first()
                                .uppercase(),

                            style = MaterialTheme.typography.titleMedium
                        )

                    }

                }

            }



            Spacer(
                modifier = Modifier.width(12.dp)
            )



            Text(

                text = userName,

                style = MaterialTheme.typography.titleMedium

            )


        }

    }

}