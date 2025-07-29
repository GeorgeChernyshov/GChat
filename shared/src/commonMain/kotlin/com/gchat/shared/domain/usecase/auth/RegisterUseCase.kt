package com.gchat.shared.domain.usecase.auth

import com.gchat.shared.domain.model.auth.RegisterUserCommand
import com.gchat.shared.domain.model.auth.AuthResultDetails
import com.gchat.shared.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        command: RegisterUserCommand
    ) = authRepository.register(command)
}
