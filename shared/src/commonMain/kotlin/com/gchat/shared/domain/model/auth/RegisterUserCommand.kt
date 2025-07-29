package com.gchat.shared.domain.model.auth

data class RegisterUserCommand(
    val phoneNumber: String,
    val password: String
)
