package com.gchat.shared.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gchat.shared.data.model.auth.LoginRequest
import com.gchat.shared.domain.model.auth.RegisterUserCommand
import com.gchat.shared.domain.usecase.LoginUseCase
import com.gchat.shared.domain.usecase.auth.RegisterUseCase
import com.gchat.shared.presentation.auth.ui.login.LoginScreenUiState
import com.gchat.shared.presentation.auth.ui.register.RegisterScreenUiState
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
    data class Register(
        val phoneNumber: String,
        val password: String
    ) : AuthIntent()

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

    // Login Screen UI State
    private val _loginUiState = MutableStateFlow(LoginScreenUiState())
    val loginUiState: StateFlow<LoginScreenUiState> = _loginUiState.asStateFlow()

    fun onLoginPhoneNumberChange(value: String) {
        _loginUiState.value = _loginUiState.value.copy(phoneNumber = value)
    }

    fun onLoginPasswordChange(value: String) {
        _loginUiState.value = _loginUiState.value.copy(password = value)
    }

    // Register Screen UI State
    private val _registerUiState = MutableStateFlow(RegisterScreenUiState())
    val registerUiState: StateFlow<RegisterScreenUiState> = _registerUiState.asStateFlow()

    fun onRegisterPhoneNumberChange(value: String) {
        _registerUiState.value = _registerUiState.value.copy(phoneNumber = value)
    }

    fun onRegisterPasswordChange(value: String) {
        _registerUiState.value = _registerUiState.value.copy(password = value)
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        _registerUiState.value = _registerUiState.value.copy(confirmPassword = value)
    }

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Register -> register(
                RegisterUserCommand(
                    phoneNumber = intent.phoneNumber,
                    password = intent.password
                )
            )

            is AuthIntent.Login -> login(intent.request)
            AuthIntent.ClearError ->
                _state.value = _state.value.copy(error = null)

            AuthIntent.ClearSuccessMessage ->
                _state.value = _state.value.copy(successMessage = null)
        }
    }

    private fun register(command: RegisterUserCommand) {
        viewModelScope.launch {
            // Update general loading state
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )
            // Update screen-specific UI state
            _registerUiState.value = _registerUiState.value.copy(
                isLoading = true,
                errorMessage = null,
                registrationSuccessful = false
            )

            val result = registerUseCase(command)
            when {
                result.isSuccess -> {
                    // Update general state
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Registration successful!"
                    )
                    // Update screen-specific UI state
                    _registerUiState.value = _registerUiState.value.copy(
                        isLoading = false,
                        registrationSuccessful = true
                    )
                    _events.value = AuthEvent.RegistrationSuccess
                }

                result.isFailure -> {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                    // Update general state
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                    // Update screen-specific UI state
                    _registerUiState.value = _registerUiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage,
                        registrationSuccessful = false
                    )
                    _events.value = AuthEvent.Error(errorMessage)
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

    // New function to reset registration successful flag after navigation
    fun onRegistrationHandled() {
        _registerUiState.value = _registerUiState.value.copy(registrationSuccessful = false)
    }
}