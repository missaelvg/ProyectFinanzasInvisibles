package com.example.proyectfinanzasinvisibles.sensores

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat

@Composable
fun RequestLocationPermissions(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    var showBackgroundDialog by remember { mutableStateOf(false) }

    val foregroundLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                showBackgroundDialog = true
            } else {
                onPermissionGranted()
            }
        } else {
            onPermissionDenied()
        }
    }

    val backgroundLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            // Background permission is optional for some features, but the user asked for "Always"
            onPermissionDenied()
        }
    }

    if (showBackgroundDialog) {
        AlertDialog(
            onDismissRequest = { 
                showBackgroundDialog = false
                onPermissionGranted() // Contamos como concedido el primer nivel
            },
            title = { Text("Permiso de Ubicación en Segundo Plano") },
            text = { Text("Para registrar tus gastos automáticamente basados en tu ubicación, selecciona 'Permitir todo el tiempo' en la siguiente pantalla.") },
            confirmButton = {
                TextButton(onClick = {
                    showBackgroundDialog = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        backgroundLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }
                }) {
                    Text("Configurar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBackgroundDialog = false
                    onPermissionGranted()
                }) {
                    Text("Ahora no")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        foregroundLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
