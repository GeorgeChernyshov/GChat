package com.gchat.shared.presentation.auth.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.gchat.shared.data.model.auth.LoginRequest
import com.gchat.shared.presentation.auth.AuthEvent
import com.gchat.shared.presentation.auth.AuthIntent
import com.gchat.shared.presentation.auth.AuthViewModel
import com.gchat.shared.presentation.auth.ui.register.RegisterScreen
import com.gchat.shared.presentation.home.HomeScreen
import org.koin.compose.viewmodel.koinViewModel

object LoginScreen : Screen {
    @Composable
    override fun Content() {
        val authViewModel = koinViewModel<AuthViewModel>()
        val authState by authViewModel.state.collectAsState()
        val uiState by authViewModel.loginUiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        var passwordVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            authViewModel.events.collect { event ->
                when (event) {
                    AuthEvent.LoginSuccess -> {
                        // Navigate to home screen
                        // Replace current stack with Home screen
                        navigator.popUntilRoot()
                        navigator.push(HomeScreen) // Assuming HomeScreen is defined in Screens.kt
                    }
                    AuthEvent.RegistrationSuccess -> { /* Handled by RegisterScreen */ }
                    is AuthEvent.Error -> { /* Display error in UI */ }
                    null -> {}
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Login", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = authViewModel::onLoginPhoneNumberChange,
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = authViewModel::onLoginPasswordChange,
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Button(
                onClick = {
                    val intent = AuthIntent.Login(
                        request = LoginRequest(uiState.phoneNumber, uiState.password)
                    )

                    authViewModel.onIntent(intent)
                },
                enabled = !authState.isLoading,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text(
                    text = if (authState.isLoading)
                        "Logging in..."
                    else "Login"
                )
            }

            authState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Don't have an account? Register",
                modifier = Modifier.clickable {
                    navigator.push(RegisterScreen)
                }.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
