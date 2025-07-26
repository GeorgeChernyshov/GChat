package com.gchat.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.gchat.shared.navigation.LoginNavScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(LoginNavScreen)
    }
}
