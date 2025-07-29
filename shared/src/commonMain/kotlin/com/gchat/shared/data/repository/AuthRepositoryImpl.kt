package com.gchat.shared.data.repository

import com.gchat.shared.data.source.AuthDataSource
import com.gchat.shared.domain.repository.AuthRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerializationException
import co.touchlab.kermit.Logger
// Import for DTO (still needed by AuthDataSource)
import com.gchat.shared.data.model.auth.RegisterRequest
import com.gchat.shared.data.model.auth.LoginRequest // Keep for login method
// Import for Domain models
import com.gchat.shared.domain.model.auth.RegisterUserCommand
import com.gchat.shared.domain.model.auth.AuthResultDetails
// Import for Mapper
import com.gchat.shared.data.model.mappers.toAuthResultDetails
import com.gchat.shared.data.model.auth.AuthResponse


class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun register(command: RegisterUserCommand): Result<AuthResultDetails> {
        // Map Domain command to Data DTO
        val apiRequest = RegisterRequest(
            phoneNumber = command.phoneNumber,
            password = command.password
        )
        return try {
            val response = authDataSource.register(apiRequest) // Assuming AuthDataSource.register still takes RegisterRequest
            when (response.status) {
                HttpStatusCode.Created -> {
                    // Assuming on success, the body is TokenResponse
                    val tokenResponse = response.body<AuthResponse>()
                    Result.success(tokenResponse.toAuthResultDetails()) // Map DTO to Domain model
                }
                HttpStatusCode.BadRequest -> {
                    Result.failure(Exception("Bad request: ${response.status.description}"))
                }
                HttpStatusCode.Conflict -> {
                    Result.failure(Exception("User with this phone number already exists."))
                }
                else -> {
                    Result.failure(Exception("Registration failed: ${response.status.description}"))
                }
            }
        } catch (e: RedirectResponseException) {
            Logger.e("AuthRepositoryImpl", e) {
                "RedirectResponseException: ${e.response.status.description}"
            }

            Result.failure(Exception("Redirection error: ${e.response.status.description}"))
        } catch (e: ClientRequestException) {
            Logger.e("AuthRepositoryImpl", throwable = e) {
                "ClientRequestException: ${e.response.status.description}"
            }

            Result.failure(Exception("Client error: ${e.response.status.description}"))
        } catch (e: ServerResponseException) {
            Logger.e( "AuthRepositoryImpl", throwable = e) {
                "ServerResponseException: ${e.response.status.description}"
            }

            Result.failure(Exception("Server error: ${e.response.status.description}"))
        } catch (e: SerializationException) {
            Logger.e("AuthRepositoryImpl", throwable = e) {
                "SerializationException: ${e.message}"
            }

            Result.failure(Exception("Failed to parse server response."))
        } catch (e: Exception) {
            Logger.e("AuthRepositoryImpl", throwable = e) {
                "Unknown error during registration: ${e.message}"
            }

            Result.failure(Exception("An unexpected error occurred."))
        }
    }

    // Login method still uses old models for now, will be refactored separately
    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        TODO("Login method to be refactored with domain models")
    }

    override suspend fun getAccessToken(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getRefreshToken(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearTokens() {
        TODO("Not yet implemented")
    }
}
