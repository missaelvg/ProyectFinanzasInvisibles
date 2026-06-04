package com.example.proyectfinanzasinvisibles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Azul claro
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
