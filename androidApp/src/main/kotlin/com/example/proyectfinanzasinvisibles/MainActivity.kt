package com.example.proyectfinanzasinvisibles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectfinanzasinvisibles.auth.backend.AuthRepository
import com.example.proyectfinanzasinvisibles.auth.ui.AuthScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository()

        setContent {
            var isLoggedIn by remember { mutableStateOf(authRepository.isUserLoggedIn()) }

            if (isLoggedIn) {
                App(onLogout = {
                    authRepository.logout()
                    isLoggedIn = false
                })
            } else {
                AuthScreen(onLoginSuccess = {
                    isLoggedIn = true
                })
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}