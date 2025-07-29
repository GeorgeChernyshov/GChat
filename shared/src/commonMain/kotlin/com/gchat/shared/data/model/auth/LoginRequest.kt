package com.gchat.shared.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val phoneNumber: String,
    val password: String
)