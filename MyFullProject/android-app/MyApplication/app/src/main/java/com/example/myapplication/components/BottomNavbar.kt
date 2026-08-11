package com.example.myapplication.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun BottomNavBar(navController: NavHostController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("home")
            },
            label = {
                Text("Home")
            },
            icon = {}
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("profile")
            },
            label = {
                Text("Profile")
            },
            icon = {}
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("messages")
            },
            label = {
                Text("Messages")
            },
            icon = {}
        )
    }
}