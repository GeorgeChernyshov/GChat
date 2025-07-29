package com.gchat.shared.presentation.auth.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.gchat.shared.data.model.auth.RegisterRequest
import com.gchat.shared.presentation.auth.AuthIntent
import com.gchat.shared.presentation.auth.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

object RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel = koinViewModel<AuthViewModel>()
        val uiState by authViewModel.registerUiState.collectAsState()

        // Handle navigation on successful registration
        LaunchedEffect(uiState.registrationSuccessful) {
            if (uiState.registrationSuccessful) {
                navigator.pop()
                authViewModel.onRegistrationHandled()
            }
        }

        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Register", style = MaterialTheme.typography.headlineMedium)
                TextField(
                    value = uiState.phoneNumber,
                    onValueChange = authViewModel::onRegisterPhoneNumberChange,
                    label = { Text("Phone Number") },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextField(
                    value = uiState.password,
                    onValueChange = authViewModel::onRegisterPasswordChange,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextField(
                    value = uiState.confirmPassword,
                    onValueChange = authViewModel::onRegisterConfirmPasswordChange,
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = { 
                        authViewModel.onIntent(
                            AuthIntent.Register(
                                phoneNumber = uiState.phoneNumber,
                                password = uiState.password
                            )
                        )
                    },
                    enabled = !uiState.isLoading, // Disable button when loading
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text("Register")
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                }

                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
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