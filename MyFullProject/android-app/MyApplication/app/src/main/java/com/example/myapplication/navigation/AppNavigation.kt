package com.example.myapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.components.BottomNavBar
import com.example.myapplication.screens.HomeScreen
import com.example.myapplication.screens.LoginScreen
import com.example.myapplication.screens.MessagesScreen
import com.example.myapplication.screens.ProfileScreen
import com.example.myapplication.screens.RegisterScreen
import com.example.myapplication.session.SessionManager
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.myapplication.screens.ActivityDetailsScreen
import com.example.myapplication.screens.ChatScreen
import com.example.myapplication.screens.ProfileViewScreen
import com.example.myapplication.screens.FriendsScreen
import com.example.myapplication.screens.PrivateChatScreen
import com.example.myapplication.session.SessionManager.currentUserId

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("home") {
                HomeScreen(
                    userId = SessionManager.currentUserId
                )
            }

            composable("profile") {

                if (SessionManager.isLoggedIn()) {
                    ProfileScreen(currentUserId = SessionManager.currentUserId , navController)
                } else {
                    LoginScreen(navController)
                }
            }

            composable("login") {
                LoginScreen(navController)
            }

            composable("register") {
                RegisterScreen(navController)
            }

            composable("Messages") {
                MessagesScreen(
                    userId = SessionManager.getUserId(),
                    navController
                )
            }


            composable(
                route = "chat/{chatId}",
                arguments = listOf(
                    navArgument("chatId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->

                val chatId = backStackEntry.arguments!!.getLong("chatId")

                ChatScreen(chatId , userId = SessionManager.getUserId()!!, navController)
            }



            composable(
                route = "userProfile/{userId}",
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->

                val userId =
                    backStackEntry.arguments!!
                        .getLong("userId")

                ProfileViewScreen(
                    userId = userId,
                    navController = navController
                )
            }


            composable(
                route = "activityDetails/{chatId}",
                arguments = listOf(
                    navArgument("chatId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->

                val chatId = backStackEntry.arguments!!.getLong("chatId")

                ActivityDetailsScreen(
                    chatId = chatId,
                    navController = navController
                )
            }


            composable(
                "friends/{userId}"
            ) { backStackEntry ->

                FriendsScreen(
                    userId = backStackEntry.arguments!!
                        .getString("userId")!!
                        .toLong(),
                    navController = navController
                )

            }

            composable(
                "privateChat/{targetUserId}"
            ){

                val targetUserId =
                    it.arguments
                        ?.getString("targetUserId")
                        ?.toLong()


                PrivateChatScreen(
                    userId = currentUserId!!,
                    targetUserId = targetUserId!!,
                    navController = navController
                )
            }


        }
    }
}