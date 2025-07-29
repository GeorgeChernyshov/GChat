package com.gchat.shared.di

import com.gchat.shared.data.repository.AuthRepositoryImpl
import com.gchat.shared.data.source.AuthDataSource
import com.gchat.shared.domain.repository.AuthRepository
import com.gchat.shared.domain.usecase.LoginUseCase
import com.gchat.shared.domain.usecase.auth.RegisterUseCase
import com.gchat.shared.presentation.auth.AuthViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val httpModule = module {
    single<HttpClient> { getHttpClient() }
}

val dataSourceModule = module {
    includes(httpModule)
    singleOf(::AuthDataSource)
}

val repositoryModule = module {
    includes(dataSourceModule)
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}

val useCaseModule = module {
    includes(repositoryModule)
    singleOf(::LoginUseCase)
    singleOf(::RegisterUseCase)
}

val viewModelModule = module {
    includes(useCaseModule)
    viewModelOf(::AuthViewModel)
}