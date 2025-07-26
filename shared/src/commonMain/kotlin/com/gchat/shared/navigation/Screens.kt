package com.gchat.shared.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.gchat.shared.presentation.auth.ui.login.LoginScreen as LoginScreenSource
import com.gchat.shared.presentation.auth.ui.register.RegisterScreen as RegisterScreenSource
import com.gchat.shared.presentation.home.HomeScreen as HomeScreenSource

internal object LoginNavScreen : Screen {
    @Composable
    override fun Content() {
        LoginScreenSource.Content()
    }
}

internal object RegisterNavScreen : Screen {
    @Composable
    override fun Content() {
        RegisterScreenSource.Content()
    }
}

internal object HomeNavScreen : Screen {
    @Composable
    override fun Content() {
        HomeScreenSource.Content()
    }
}
