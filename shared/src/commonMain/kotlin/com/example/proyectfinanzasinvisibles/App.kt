package com.example.proyectfinanzasinvisibles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

import proyectfinanzasinvisibles.shared.generated.resources.Res
import proyectfinanzasinvisibles.shared.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Dashboard - Finanzas Invisibles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                elevation = 4.dp,
                backgroundColor = Color(0xFFE3F2FD) // Azul claro
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gasto Semanal (Hormiga)", fontWeight = FontWeight.Bold)
                    Text("$450.00 MXN", fontSize = 32.sp, color = Color(0xFFD32F2F)) // Rojo alerta
                    Text("¡Cuidado! Estás superando tu límite de antojitos.", color = Color.Gray)
                }
            }
        }
    }
}