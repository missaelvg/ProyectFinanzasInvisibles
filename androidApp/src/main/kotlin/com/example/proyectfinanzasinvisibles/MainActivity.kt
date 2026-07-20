package com.example.proyectfinanzasinvisibles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectfinanzasinvisibles.sync.backend.WorkManagerScheduler
import com.example.proyectfinanzasinvisibles.auth.backend.AuthRepository
import com.example.proyectfinanzasinvisibles.auth.ui.AuthScreen
import com.example.proyectfinanzasinvisibles.ui.components.LoadingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WorkManagerScheduler.programarSincronizacionPeriodica(applicationContext)

        val authRepository = AuthRepository()

        setContent {
            var isAppReady by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(authRepository.isUserLoggedIn()) }

            LaunchedEffect(Unit) {
                // Verification of session persistence
                if (isLoggedIn) {
                    val result = authRepository.getUserProfile()
                    if (result.isFailure) {
                        // Only logout if the token is completely invalid/expired
                        // Firebase handles persistence automatically, so we just double check
                        // if we can actually reach the data.
                        // authRepository.logout() // Uncomment if you want strict session verification
                        // isLoggedIn = false
                    }
                }
                isAppReady = true
            }

            if (!isAppReady) {
                LoadingScreen()
            } else {
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
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
