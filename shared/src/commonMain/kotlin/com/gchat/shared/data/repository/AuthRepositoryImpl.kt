package com.gchat.shared.data.repository

import com.gchat.shared.data.source.AuthDataSource
import com.gchat.shared.domain.repository.AuthRepository
import com.gchat.shared.models.AuthResponse
import com.gchat.shared.models.LoginRequest
import com.gchat.shared.models.RegisterRequest

class AuthRepositoryImpl(private val authDataSource: AuthDataSource) : AuthRepository {

    // TODO: Replace with secure platform-specific storage (e.g., EncryptedSharedPreferences, Keychain)
    private var accessToken: String? = null
    private var refreshToken: String? = null

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = authDataSource.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = authDataSource.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAccessToken(): String? {
        return accessToken
    }

    override fun getRefreshToken(): String? {
        return refreshToken
    }

    override fun saveTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    override fun clearTokens() {
        this.accessToken = null
        this.refreshToken = null
    }
}