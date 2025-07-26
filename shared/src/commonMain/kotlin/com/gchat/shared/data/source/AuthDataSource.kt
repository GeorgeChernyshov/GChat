package com.gchat.shared.data.source

import com.gchat.shared.models.AuthResponse
import com.gchat.shared.models.LoginRequest
import com.gchat.shared.models.RegisterRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthDataSource(private val httpClient: HttpClient) {
    suspend fun register(request: RegisterRequest): AuthResponse {
        return httpClient.post("http://127.0.0.1:8080/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun login(request: LoginRequest): AuthResponse {
        return httpClient.post("http://127.0.0.1:8080/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}