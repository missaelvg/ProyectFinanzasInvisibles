package com.example.proyectfinanzasinvisibles

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.proyectfinanzasinvisibles.sync.backend.WorkManagerScheduler
import com.example.proyectfinanzasinvisibles.backend.repositories.AuthRepository
import com.example.proyectfinanzasinvisibles.auth.ui.AuthScreen
import com.example.proyectfinanzasinvisibles.frontend.ui.components.LoadingScreen
import com.example.proyectfinanzasinvisibles.frontend.App
import com.example.proyectfinanzasinvisibles.frontend.ui.theme.StealthMonochromeTheme
import com.example.proyectfinanzasinvisibles.backend.repositories.GastoRepository
import com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase
import kotlinx.coroutines.withTimeoutOrNull
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WorkManagerScheduler.programarSincronizacionPeriodica(applicationContext)

        val authRepository = AuthRepository()

        setContent {
            var isAppReady by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(authRepository.isUserLoggedIn()) }

            val context = LocalContext.current
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { _ -> }

            LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    // Entramos a la App de inmediato para que no se quede trabado
                    isAppReady = true 
                    
                    // La sincronización ocurre en segundo plano
                    try {
                        val profileResult = withTimeoutOrNull(5000) { authRepository.getUserProfile() }
                        if (profileResult?.isSuccess == true) {
                            authRepository.actualizarRacha()
                            val firebaseRepo = GastoRepository()
                            val gastosFirebase = firebaseRepo.obtenerGastos()
                            GastoDatabase.inicializarGastos(gastosFirebase)
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error en sync de fondo: ${e.message}")
                    }
                } else {
                    GastoDatabase.limpiarBaseDeDatos()
                    isAppReady = true
                }
            }

            StealthMonochromeTheme {
                if (!isAppReady) {
                    LoadingScreen()
                } else {
                    if (isLoggedIn) {
                        App(onLogout = {
                            authRepository.logout()
                            GastoDatabase.limpiarBaseDeDatos()
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
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
