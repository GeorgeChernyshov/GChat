package com.gchat.shared.data.model.auth

import com.gchat.shared.models.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)