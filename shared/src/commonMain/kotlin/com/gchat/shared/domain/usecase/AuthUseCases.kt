package com.gchat.shared.domain.usecase

import com.gchat.shared.domain.repository.AuthRepository
import com.gchat.shared.models.AuthResponse
import com.gchat.shared.models.LoginRequest
import com.gchat.shared.models.RegisterRequest

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(request: RegisterRequest): Result<AuthResponse> {
        return repository.register(request)
    }
}

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(request: LoginRequest): Result<AuthResponse> {
        return repository.login(request)
    }
}