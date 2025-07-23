package com.gchat.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val phoneNumber: String,
    val password: String,
    val displayName: String
)

@Serializable
data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)
