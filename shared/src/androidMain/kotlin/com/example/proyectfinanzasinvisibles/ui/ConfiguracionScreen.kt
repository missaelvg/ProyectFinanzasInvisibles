package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.provider.Settings

@Composable
actual fun ConfiguracionScreen() {
    val context = LocalContext.current
    var isServiceEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Configuración del Lector", fontWeight = FontWeight.Bold)
        
        Switch(
            checked = isServiceEnabled,
            onCheckedChange = { 
                isServiceEnabled = it
                // Para activar el lector de notificaciones, el usuario debe dar permiso en Ajustes
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        )
        Text("Activar lectura en segundo plano (Requiere permiso de sistema)")
        
        Button(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Abrir Ajustes de Notificaciones")
        }
    }
}
