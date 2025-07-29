package com.gchat.shared.data.source

import com.gchat.shared.data.model.auth.AuthResponse
import com.gchat.shared.data.model.auth.LoginRequest
import com.gchat.shared.data.model.auth.RegisterRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*

class AuthDataSource(private val httpClient: HttpClient) {
    suspend fun register(request: RegisterRequest): HttpResponse {
        return httpClient.post("http://127.0.0.1:8080/auth/register") { // Corrected endpoint and return type
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun login(request: LoginRequest): AuthResponse {
        return httpClient.post("http://127.0.0.1:8080/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}