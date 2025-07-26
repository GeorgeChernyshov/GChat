package com.gchat.shared.presentation.auth.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

object RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Register", style = MaterialTheme.typography.headlineMedium)
                TextField(
                    value = "",
                    onValueChange = { /* TODO: Update phone number */ },
                    label = { Text("Phone Number") },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextField(
                    value = "",
                    onValueChange = { /* TODO: Update password */ },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextField(
                    value = "",
                    onValueChange = { /* TODO: Update confirm password */ },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = { /* TODO: Handle registration */ },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text("Register")
                }
                Button(
                    onClick = { navigator.pop() },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Already have an account? Login")
                }
            }
        }
    }
}