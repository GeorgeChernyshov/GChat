package com.gchat.shared.presentation.auth.ui.register

data class RegisterScreenUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registrationSuccessful: Boolean = false
)