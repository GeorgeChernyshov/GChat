package com.gchat.shared.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gchat.shared.domain.usecase.LoginUseCase
import com.gchat.shared.domain.usecase.RegisterUseCase
import com.gchat.shared.models.LoginRequest
import com.gchat.shared.models.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// MVI State
data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

// MVI Events (one-time actions)
sealed class AuthEvent {
    object RegistrationSuccess : AuthEvent()
    object LoginSuccess : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

// MVI Intents (user actions)
sealed class AuthIntent {
    data class Register(val request: RegisterRequest) : AuthIntent()
    data class Login(val request: LoginRequest) : AuthIntent()
    object ClearError : AuthIntent()
    object ClearSuccessMessage : AuthIntent()
}

class AuthViewModel(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _events = MutableStateFlow<AuthEvent?>(null)
    val events: StateFlow<AuthEvent?> = _events.asStateFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Register -> register(intent.request)
            is AuthIntent.Login -> login(intent.request)
            AuthIntent.ClearError ->
                _state.value = _state.value.copy(error = null)

            AuthIntent.ClearSuccessMessage ->
                _state.value = _state.value.copy(successMessage = null)
        }
    }

    private fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            val result = registerUseCase(request)
            when {
                result.isSuccess -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Registration successful!"
                    )

                    _events.value = AuthEvent.RegistrationSuccess
                }

                result.isFailure -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )

                    _events.value = AuthEvent.Error(
                        result.exceptionOrNull()
                            ?.message
                            ?: "Unknown error"
                    )
                }
            }
        }
    }

    private fun login(request: LoginRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            val result = loginUseCase(request)
            when {
                result.isSuccess -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        successMessage = "Login successful!"
                    )

                    _events.value = AuthEvent.LoginSuccess
                }

                result.isFailure -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )

                    _events.value = AuthEvent.Error(
                        result.exceptionOrNull()
                            ?.message
                            ?: "Unknown error"
                    )
                }
            }
        }
    }
}