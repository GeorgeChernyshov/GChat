package com.gchat.shared.domain.repository

import com.gchat.shared.models.AuthResponse
import com.gchat.shared.models.LoginRequest
import com.gchat.shared.models.RegisterRequest

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String)
    fun clearTokens()
}