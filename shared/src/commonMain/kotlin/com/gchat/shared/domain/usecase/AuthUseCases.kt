package com.gchat.shared.domain.usecase

import com.gchat.shared.data.model.auth.AuthResponse
import com.gchat.shared.data.model.auth.LoginRequest
import com.gchat.shared.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(request: LoginRequest): Result<AuthResponse> {
        return repository.login(request)
    }
}