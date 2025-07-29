package com.gchat.shared.domain.repository

import com.gchat.shared.data.model.auth.AuthResponse
import com.gchat.shared.data.model.auth.LoginRequest
// Removed: import com.gchat.shared.data.model.auth.RegisterRequest - no longer used here
import com.gchat.shared.domain.model.auth.RegisterUserCommand // New import
import com.gchat.shared.domain.model.auth.AuthResultDetails // New import

interface AuthRepository {
    suspend fun register(command: RegisterUserCommand): Result<AuthResultDetails> // Signature updated
    suspend fun login(request: LoginRequest): Result<AuthResponse> // Keeping LoginRequest for now, can refactor next
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}
