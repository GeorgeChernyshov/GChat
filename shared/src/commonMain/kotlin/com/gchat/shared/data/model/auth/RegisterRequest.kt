package com.gchat.shared.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val phoneNumber: String,
    val password: String
)