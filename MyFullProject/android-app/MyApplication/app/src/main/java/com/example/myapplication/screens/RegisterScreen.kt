package com.example.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.models.RegisterRequest
import com.example.myapplication.models.User
import com.example.myapplication.network.RetrofitInstance

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(
    navController: NavController
) {

    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Register")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Country") }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {

                RetrofitInstance.api.register(
                    RegisterRequest(
                        name,
                        email,
                        password,
                        country
                    )
                ).enqueue(
                    object : Callback<User> {

                        override fun onResponse(
                            call: Call<User>,
                            response: Response<User>
                        ) {

                            if(response.isSuccessful) {

                                navController.navigate("login")
                            }
                        }

                        override fun onFailure(
                            call: Call<User>,
                            t: Throwable
                        ) {
                        }
                    }
                )

            }
        ) {
            Text("Register")
        }
    }
}